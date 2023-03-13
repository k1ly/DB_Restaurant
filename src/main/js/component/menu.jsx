import React from "react";
import ReactDOM from "react-dom/client";
import axios from "axios";
import {PageComponent, PaginationComponent, SearchComponent, SpinnerComponent} from "./page.jsx";
import {authenticate} from "../authentication.js";
import "bootstrap/dist/css/bootstrap.min.css";

class MenuDishComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {description: false};
    }

    render() {
        return <div className={'card'}>
            <div className={'d-flex'}>
                <div className={'dish-image p-1'}>
                    <img src={this.props.dish.imageUrl} alt={'Image'}
                         className={'position-relative w-100 h-100 rounded'}/>
                    {this.props.dish.discount && this.props.dish.discount > 0 ?
                        <div
                            className={'dish-discount position-absolute top-0 start-0'}>-{this.props.dish.discount}%
                        </div> : null}
                </div>
                <div className={'w-100'}>
                    <div className={'card-header'}>
                        <div className={'card-title fs-5'}>{this.props.dish.name}</div>
                    </div>
                    <div className={'card-body'}>
                        <div className={`card-text${this.state.description ? null : 'text-truncate'}`}
                             onClick={e => this.setState({description: !this.state.description})}>
                            {this.props.dish.description}
                        </div>
                    </div>
                    <div className={'card-footer h-100'}>
                        <div className={'d-flex justify-content-between'}>
                            <div
                                className={'d-flex justify-content-between w-25'}>
                                <div>{this.props.dish.weight} г.</div>
                                <div>|</div>
                                <div>{this.props.dish.price} р.</div>
                            </div>
                            <div>
                                <button
                                    className={'btn btn-outline-primary btn-sm'}
                                    onClick={e => this.props.addOrderItem()}>
                                    Добавить в корзину
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>;
    }
}

class MenuContentComponent extends React.Component {
    constructor(props) {
        super(props);
        const params = new URLSearchParams(window.location.search);
        let sort = params.get('sort');
        this.state = {
            dishes: {number: 0, size: 10},
            sort: sort && sort.match('^name|price|discount,asc|desc$') ? {
                attribute: sort.substring(0, sort.indexOf(',')),
                order: sort.substring(sort.indexOf(',') + 1)
            } : {attribute: 'id', order: 'asc'},
            filter: props.filter ? props.filter : ''
        };
        this.loadDishes = this.loadDishes.bind(this);
        this.handleSort = this.handleSort.bind(this);
        this.addOrderItem = this.addOrderItem.bind(this);
    }

    loadDishes(page) {
        axios.get(`/api/dishes?${this.state.filter ? `filter=${this.state.filter}` :
                `${this.props.category ? `category=${this.props.category.id}` : ''}`}` +
            `&page=${page ? page : this.state.dishes.number}&size=${this.state.dishes.size}` +
            `&sort=${this.state.sort.attribute},${this.state.sort.order}`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({dishes: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    handleSort(sort) {
        let attribute = sort.match('▲|▼') ? sort.substring(0, sort.indexOf(' ')) : sort;
        let order = sort.match('▲') ? 'desc' : 'asc';
        location.href = `/menu?${this.state.filter ? `filter=${this.state.filter}` :
                `${this.props.category ? `category=${this.props.category.id}` : ''}`}` +
            `&sort=${attribute},${order}`;
    }

    addOrderItem(dish) {
        if (this.state.user) {
            axios.post(`/api${!this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$') ? '/cookie/cart' : '/order-items'}/add`,
                JSON.stringify({
                    quantity: 1,
                    dish: this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$') ? {id: dish.id} : dish.id,
                    order: this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$') ?
                        {id: this.state.user.order.id} : null
                }), {
                    headers: {
                        'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                        'Content-Type': 'application/json'
                    }
                })
                .then(result => {
                    alert('Добавлено в корзину!');
                }).catch(error => {
                console.error('Error:', error.response ? error.response.data : error);
                if (error.response.status === 401)
                    location.href = '/error/401';
            })
        }
    }

    componentDidMount() {
        authenticate(user => this.setState.bind(this)({user: user}));
        this.loadDishes();
    }

    render() {
        return this.state.dishes.content ?
            <div className={'h-100'}>
                {this.props.category ?
                    <div
                        className={'my-3 border-3 border-bottom text-center text-white fs-2'}>
                        {this.props.category.name}</div> : null}
                <SearchComponent filter={this.state.filter}
                                 setFilter={filter => this.setState({filter: filter})}
                                 loadPage={p => location.href = `/menu?filter=${this.state.filter}`}/>
                {this.state.dishes.totalElements > 0 ?
                    <div>
                        <PaginationComponent page={this.state.dishes} loadPage={this.loadDishes}/>
                        <div>
                            <div className={'d-inline-flex float-end me-4'}>
                                <label htmlFor={'sort'} className={'me-3 text-end text-nowrap text-white lh-lg'}>
                                    Сортировать по
                                </label>
                                <select name={'sort'}
                                        onClick={e => e.detail === 0 ? this.handleSort(e.target.value) : null}
                                        id={'sort'} className={'form-select'}>
                                    <option selected={this.state.sort.attribute === 'id'} disabled={true}>
                                        ...
                                    </option>
                                    <option value={`name${this.state.sort.attribute === 'name' ?
                                        (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : ''}`}
                                            selected={this.state.sort.attribute === 'name'}>
                                        Имя{this.state.sort.attribute === 'name' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </option>
                                    <option value={`price${this.state.sort.attribute === 'price' ?
                                        (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : ''}`}
                                            selected={this.state.sort.attribute === 'price'}>
                                        Цена{this.state.sort.attribute === 'price' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </option>
                                    <option value={`discount${this.state.sort.attribute === 'discount' ?
                                        (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : ''}`}
                                            selected={this.state.sort.attribute === 'discount'}>
                                        Скидка{this.state.sort.attribute === 'discount' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </option>
                                </select>
                            </div>
                            <table className={'table table-borderless'}>
                                <tbody>{this.state.dishes.content.map(dish =>
                                    <tr key={dish.id}>
                                        <td>
                                            <MenuDishComponent dish={dish}
                                                               addOrderItem={() => this.addOrderItem(dish)}/>
                                        </td>
                                    </tr>)}
                                </tbody>
                            </table>
                        </div>
                    </div>
                    : (this.props.category ?
                        <div className={'d-flex justify-content-center align-items-center h-100'}>
                            <div className={'text-white fs-4'}>Категория "{this.props.category.name}" пока пуста</div>
                        </div>
                        : this.props.filter ?
                            <div className={'d-flex justify-content-center align-items-center h-100'}>
                                <div className={'text-white fs-4'}>
                                    По запросу "{this.props.filter}" ничего найдено
                                </div>
                            </div> : null)}
            </div>
            : <SpinnerComponent className={'text-white'}/>;
    }
}

class MenuComponent extends React.Component {
    constructor(props) {
        super(props);
        const params = new URLSearchParams(window.location.search);
        this.state = {
            filter: params.get('filter'),
            tab: params.get('category')
        };
    }

    componentDidMount() {
        if (!this.state.filter) {
            if (this.state.tab) {
                axios.get(`/api/categories/${this.state.tab}`, {
                    headers: {
                        'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                        'Accept': 'application/json'
                    }
                }).then(result => {
                    this.setState({category: result.data});
                }).catch(error => {
                    console.error('Error:', error.response ? error.response.data : error);
                    if (error.response.status === 401)
                        location.href = '/error/401';
                })
            } else location.href = '?category=1';
        }
        axios.get(`/api/categories/all?sort=id,asc`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({categories: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    render() {
        return <div className={'row mx-0 h-100'}>
            {this.state.categories ?
                <nav className={'col-3 nav flex-column border-bottom border-secondary rounded-start'}>
                    {this.state.categories.map(category =>
                        <a key={category.id}
                           className={`category-nav-link px-3 py-4 text-center fw-bold fs-6
                               ${this.state.category && category.id === this.state.category.id ? ' active' : ''}`}
                           href={`?category=${category.id}`}>
                            {category.name}
                        </a>)}
                </nav>
                : <div className={'col-3 vh-100 bg-white rounded-start'}>
                    <SpinnerComponent className={'spinner-md'}/>
                </div>}
            <div className={'col px-0 h-100'}>
                {this.state.category || this.state.filter ?
                    <MenuContentComponent category={this.state.category} filter={this.state.filter}/>
                    : <div className={'d-flex justify-content-center align-items-center h-100'}>
                        <div className={'text-white fs-4'}>
                            Для того чтобы просмотреть меню выберите категорию или введите запрос
                        </div>
                    </div>}
            </div>
        </div>;
    }
}

ReactDOM.createRoot(document.getElementById('page-container')).render(<PageComponent><MenuComponent/></PageComponent>);