import React from "react";
import ReactDOM from "react-dom/client";
import axios from "axios";
import {PageComponent, PaginationComponent, SearchComponent, SpinnerComponent} from "./page.jsx";
import {Modal} from "react-bootstrap";
import {authenticate} from "../authentication.js";
import "bootstrap/dist/css/bootstrap.min.css";

class ImageUploadComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {url: this.props.url};
        this.handleDrag = this.handleDrag.bind(this);
        this.handleImage = this.handleImage.bind(this);
    }

    handleDrag(e) {
        e.preventDefault();
        e.stopPropagation();
        if (e.type === 'dragenter' || e.type === 'dragover')
            this.setState({drag: true});
        else if (e.type === 'dragleave')
            this.setState({drag: false});
    };

    handleImage(e) {
        e.preventDefault();
        let file;
        if (e.dataTransfer) {
            e.stopPropagation();
            this.setState({drag: false});
            file = e.dataTransfer.files[0]
        } else file = e.target.files[0];
        let reader = new FileReader();
        reader.onloadend = () => {
            this.props.setImage(file);
            this.setState({
                url: reader.result
            });
        }
        reader.readAsDataURL(file)
    }

    render() {
        return <div className={'d-flex justify-content-center p-3 w-100'}>
            <div className={'dish-image-container rounded'}
                 onDragEnter={this.handleDrag} onDragLeave={this.handleDrag} onDragOver={this.handleDrag}
                 onDrop={this.handleImage}>
                {this.state.url ?
                    <img className={'img-thumbnail w-100 h-100'} src={this.state.url} alt={'300x300'}/>
                    : <div
                        className={`d-flex align-items-center dish-image p-1 w-100 h-100 rounded text-center fs-5 fw-semibold` +
                            `${this.state.drag ? ' drag' : ''}`}>
                        Перетащите изображение сюда
                    </div>}
                <input type={'file'} accept={'image/png, image/jpeg'} className={'invisible'}
                       onChange={this.handleImage}/>
            </div>
        </div>;
    }
}

class DishFormComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: '', description: '', weight: 0,
            price: 0, discount: 0, category: {}
        };
    }

    componentDidMount() {
        axios.get(`/api/categories?sort=id,asc`, {
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

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.dish !== this.props.dish)
            this.setState(this.props.dish && this.props.dish.id ?
                {
                    name: this.props.dish.name,
                    description: this.props.dish.description,
                    weight: this.props.dish.weight,
                    price: this.props.dish.price,
                    discount: this.props.dish.discount,
                    category: this.props.dish.category
                } : {
                    name: '', description: '', weight: 0,
                    price: 0, discount: 0, category: {}
                });
    }

    render() {
        return <Modal show={!!this.props.dish} onHide={this.props.onClose}
                      backdrop={'static'} keyboard={false} centered>
            {this.props.dish ? <>
                <Modal.Header closeButton>
                    <Modal.Title>
                        <div className={'modal-title fs-4'}>
                            {this.props.dish.id ? this.props.dish.name : 'Новое блюдо'}
                        </div>
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <form className={'has-validation'}>
                        <ImageUploadComponent url={this.props.dish.imageUrl}
                                              setImage={image => this.setState({image: image})}/>
                        <div>
                            <label htmlFor={'dishName'} className={'form-label fs-5'}>Название</label>
                            <input type={'text'} name={'name'}
                                   value={this.state.name}
                                   onChange={e => this.setState({[e.target.name]: e.target.value})}
                                   id={'dishName'} className={'form-control'}/>
                        </div>
                        <div>
                            <label htmlFor={'dishDescription'} className={'form-label fs-5'}>Описание</label>
                            <textarea name={'description'}
                                      value={this.state.description}
                                      onChange={e => this.setState({[e.target.name]: e.target.value})}
                                      id={'dishDescription'} className={'form-control'}></textarea>
                        </div>
                        <div>
                            <label htmlFor={'dishWeight'} className={'form-label fs-5'}>
                                Вес <span className={'fs-6'}>(грамм)</span>
                            </label>
                            <input type={'number'} name={'weight'}
                                   value={this.state.weight}
                                   onChange={e => this.setState({[e.target.name]: e.target.value})}
                                   id={'dishWeight'} className={'form-control'}/>
                            <label htmlFor={'dishWeight'} className={'form-label fs-5'}>
                                Цена <span className={'fs-6'}>(рублей)</span>
                            </label>
                            <input type={'number'} step={'0.01'} name={'price'}
                                   value={this.state.price}
                                   onChange={e => this.setState({[e.target.name]: e.target.value})}
                                   id={'dishPrice'} className={'form-control'}/>
                            <label htmlFor={'dishWeight'} className={'form-label fs-5'}>
                                Скидка <span className={'fs-6'}>( % )</span>
                            </label>
                            <input type={'number'} name={'discount'}
                                   value={this.state.discount}
                                   onChange={e => this.setState({[e.target.name]: e.target.value})}
                                   id={'dishDiscount'} className={'form-control'}/>
                        </div>
                        {this.state.categories ?
                            <div>
                                <label htmlFor={'dishCategory'} className={'form-label fs-5'}>Категория</label>
                                <select name={'category'}
                                        value={this.state.category.id}
                                        onChange={e => this.setState({[e.target.name]: {id: parseInt(e.target.value)}})}
                                        id={'dishCategory'} className={'form-select'}>
                                    <option selected={!this.state.category.id} disabled={true}>
                                        ...
                                    </option>
                                    {this.state.categories.content.map(category =>
                                        <option key={category.id} value={category.id}>
                                            {category.name}
                                        </option>
                                    )}
                                </select>
                            </div> : null}
                    </form>
                </Modal.Body>
                <Modal.Footer>
                    {this.props.dish.id ?
                        <button className={'btn btn-primary w-100'}
                                onClick={e => this.props.updateDish({
                                    id: this.props.dish.id,
                                    name: this.state.name,
                                    description: this.state.description,
                                    weight: parseInt(this.state.weight),
                                    price: parseFloat(this.state.price),
                                    discount: parseInt(this.state.discount),
                                    category: this.state.category
                                }, this.state.image)}>
                            Изменить
                        </button>
                        : <button className={'btn btn-primary w-100'}
                                  onClick={e => this.props.addDish({
                                      name: this.state.name,
                                      description: this.state.description,
                                      weight: parseInt(this.state.weight),
                                      price: parseFloat(this.state.price),
                                      discount: parseInt(this.state.discount),
                                      category: this.state.category
                                  }, this.state.image)}>
                            Добавить
                        </button>
                    }
                </Modal.Footer>
            </> : null}
        </Modal>;
    }
}

class DishInfoComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {category: props.dish.category};
    }

    componentDidMount() {
        axios.get(`/api/categories/${this.state.category.id}`, {
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
    }

    render() {
        return <tr>
            <td className={'fw-semibold'}>{this.props.dish.id}</td>
            <td>{this.props.dish.name}</td>
            <td>{this.props.dish.weight} г.</td>
            <td>{this.props.dish.price} р.</td>
            <td>{this.props.dish.discount} %</td>
            <td>{this.state.category.name}</td>
            <td>
                <button className={'btn btn-outline-primary'}
                        onClick={e => this.props.onUpdate()}>
                    Изменить
                </button>
            </td>
            <td>
                <button className={'btn btn-outline-danger'}
                        onClick={e => this.props.deleteDish()}>
                    Удалить
                </button>
            </td>
        </tr>;
    }
}

class DishListComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            dishes: {number: 0, size: 20},
            sort: {attribute: 'id', order: 'asc'},
            filter: ''
        };
        this.loadDishes = this.loadDishes.bind(this);
        this.handleSort = this.handleSort.bind(this);
        this.saveImage = this.saveImage.bind(this);
        this.addDish = this.addDish.bind(this);
        this.updateDish = this.updateDish.bind(this);
        this.deleteDish = this.deleteDish.bind(this);
    }

    loadDishes(page, sort) {
        axios.get(`/api/dishes?${this.state.filter && this.state.filter.length > 0 ? `filter=${this.state.filter}&` : ''}` +
            `page=${page ? page : this.state.dishes.number}&size=${this.state.dishes.size}` +
            `&sort=${sort ? sort.attribute : this.state.sort.attribute},${sort ? sort.order : this.state.sort.order}`, {
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

    handleSort(attribute) {
        let sort = {
            attribute: attribute,
            order: this.state.sort.attribute !== attribute || this.state.sort.order === 'desc' ? 'asc' : 'desc'
        };
        this.setState({sort: sort});
        this.loadDishes(false, sort);
    }

    saveImage(image, callback) {
        let formData = new FormData();
        formData.append('image', image);
        axios.post('/api/dishes/image', formData, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'text/plain',
                'Content-Type': 'multipart/form-data'
            }
        }).then(result => {
            callback(result.data);
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        });
    }

    addDish(dish, image) {
        let callback = () => axios.post('/api/dishes/add',
            JSON.stringify(dish), {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            alert('Блюдо добавлено!');
            this.setState({dish: null});
            this.loadDishes();
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        });
        if (image) {
            this.saveImage(image, url => {
                dish.imageUrl = url;
                callback();
            })
        } else callback();
    }

    updateDish(dish, image) {
        let callback = () => axios.put(`/api/dishes/update/${dish.id}`,
            JSON.stringify(dish), {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            alert('Блюдо изменено!');
            this.setState({dish: null});
            this.loadDishes();
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
        if (image) {
            this.saveImage(image, url => {
                dish.imageUrl = url;
                callback();
            })
        } else callback();
    }

    deleteDish(dish) {
        axios.delete(`/api/dishes/delete/${dish.id}`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null
            }
        }).then(result => {
            alert('Блюдо удалено!');
            this.setState({dish: null});
            this.loadDishes();
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    componentDidMount() {
        this.loadDishes();
    }

    render() {
        return this.state.dishes.content ?
            <div className={'h-100'}>
                {this.state.dish ?
                    <DishFormComponent dish={this.state.dish}
                                       addDish={this.addDish}
                                       updateDish={this.updateDish}
                                       onClose={() => this.setState({dish: null})}/> : null}
                <div className={'p-4'}>
                    <button className={'btn btn-success px-2 fs-4'}
                            onClick={e => this.setState({dish: {}})}>
                        Добавить блюдо
                    </button>
                </div>
                <SearchComponent filter={this.state.filter}
                                 setFilter={filter => this.setState({filter: filter})}
                                 loadPage={this.loadDishes}/>
                {this.state.dishes.totalElements > 0 ?
                    <div className={'h-100'}>
                        <PaginationComponent page={this.state.dishes} loadPage={this.loadDishes}/>
                        <div className={'mx-5'}>
                            <table className={'table table-striped table-hover w-100'}>
                                <thead className={'table-light fw-bold'}>
                                <tr>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('id')}>
                                        ID{this.state.sort.attribute === 'id' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('name')}>
                                        Имя{this.state.sort.attribute === 'name' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('weight')}>
                                        Вес{this.state.sort.attribute === 'weight' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('price')}>
                                        Цена{this.state.sort.attribute === 'price' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('discount')}>
                                        Скидка{this.state.sort.attribute === 'discount' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('category_id')}>
                                        Категория{this.state.sort.attribute === 'category_id' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'}></th>
                                    <th scope={'col'}></th>
                                </tr>
                                </thead>
                                <tbody className={'table-group-divider'}>
                                {this.state.dishes.content.map(dish =>
                                    <DishInfoComponent key={dish.id} dish={dish}
                                                       onUpdate={() => this.setState({dish: dish})}
                                                       deleteDish={() => this.deleteDish(dish)}/>)}
                                </tbody>
                            </table>
                        </div>
                    </div>
                    : <div className={'d-flex justify-content-center align-items-center h-100'}>
                        <div className={'fs-3'}>Список блюд пуст</div>
                    </div>}
            </div>
            : <SpinnerComponent/>;
    }
}

class CategoryFormComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {name: ''};
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.category !== this.props.category)
            this.setState(this.props.category && this.props.category.id ?
                {
                    name: this.props.category.name
                } : {name: ''});
    }

    render() {
        return <Modal show={!!this.props.category} onHide={this.props.onClose}
                      backdrop={'static'} keyboard={false} centered>
            {this.props.category ? <>
                <Modal.Header closeButton>
                    <Modal.Title>
                        <div className={'modal-title fs-4'}>
                            {this.props.category.id ? this.props.category.name : 'Новая категория'}
                        </div>
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <form className={'has-validation'}>
                        <div>
                            <label htmlFor={'categoryName'} className={'form-label fs-5'}>Название</label>
                            <input type={'text'} name={'name'}
                                   value={this.state.name}
                                   onChange={e => this.setState({[e.target.name]: e.target.value})}
                                   id={'categoryName'} className={'form-control'}/>
                        </div>
                    </form>
                </Modal.Body>
                <Modal.Footer>
                    {this.props.category.id ?
                        <button className={'btn btn-primary w-100'}
                                onClick={e => this.props.updateCategory({
                                    id: this.props.category.id,
                                    name: this.state.name
                                })}>
                            Изменить
                        </button>
                        : <button className={'btn btn-primary w-100'}
                                  onClick={e => this.props.addCategory({
                                      name: this.state.name
                                  })}>
                            Добавить
                        </button>}
                </Modal.Footer>
            </> : null}
        </Modal>;
    }
}

function CategoryInfoComponent(props) {
    return <tr>
        <td className={'fw-semibold'}>{props.category.id}</td>
        <td>{props.category.name}</td>
        <td>
            <button className={'btn btn-outline-primary'}
                    onClick={e => props.onUpdate()}>
                Изменить
            </button>
        </td>
        <td>
            <button className={'btn btn-outline-danger'}
                    onClick={e => props.deleteCategory()}>
                Удалить
            </button>
        </td>
    </tr>;
}

class CategoryListComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            categories: {number: 0, size: 20},
            sort: {attribute: 'id', order: 'asc'},
            filter: ''
        };
        this.loadCategories = this.loadCategories.bind(this);
        this.handleSort = this.handleSort.bind(this);
        this.addCategory = this.addCategory.bind(this);
        this.updateCategory = this.updateCategory.bind(this);
        this.deleteCategory = this.deleteCategory.bind(this);
    }

    loadCategories(page, sort) {
        axios.get(`/api/categories?${this.state.filter && this.state.filter.length > 0 ? `filter=${this.state.filter}&` : ''}` +
            `page=${page ? page : this.state.categories.number}&size=${this.state.categories.size}` +
            `&sort=${sort ? sort.attribute : this.state.sort.attribute},${sort ? sort.order : this.state.sort.order}`, {
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

    handleSort(attribute) {
        let sort = {
            attribute: attribute,
            order: this.state.sort.attribute !== attribute || this.state.sort.order === 'desc' ? 'asc' : 'desc'
        };
        this.setState({sort: sort});
        this.loadCategories(false, sort);
    }

    addCategory(category) {
        axios.post('/api/categories/add',
            JSON.stringify(category), {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            alert('Категория добавлена!');
            this.setState({category: null});
            this.loadCategories();
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    updateCategory(category) {
        axios.put(`/api/categories/update/${category.id}`,
            JSON.stringify(category), {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            alert('Категория изменена!');
            this.setState({category: null});
            this.loadCategories();
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    deleteCategory(category) {
        axios.delete(`/api/categories/delete/${category.id}`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null
            }
        }).then(result => {
            alert('Категория удалена!');
            this.loadCategories();
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    componentDidMount() {
        this.loadCategories();
    }

    render() {
        return this.state.categories.content ?
            <div className={'h-100'}>
                <CategoryFormComponent category={this.state.category}
                                       addCategory={this.addCategory}
                                       updateCategory={this.updateCategory}
                                       onClose={() => this.setState({category: null})}/>
                <div className={'p-4'}>
                    <button className={'btn btn-success px-2 fs-4'}
                            onClick={e => this.setState({category: {}})}>
                        Добавить категорию
                    </button>
                </div>
                <SearchComponent filter={this.state.filter}
                                 setFilter={filter => this.setState({filter: filter})}
                                 loadPage={this.loadCategories}/>
                {this.state.categories.totalElements > 0 ?
                    <div className={'h-100'}>
                        <PaginationComponent page={this.state.categories} loadPage={this.loadCategories}/>
                        <div className={'mx-5'}>
                            <table className={'table table-striped table-hover w-100'}>
                                <thead className={'table-light fw-bold'}>
                                <tr>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('id')}>
                                        ID{this.state.sort.attribute === 'id' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('name')}>
                                        Имя{this.state.sort.attribute === 'name' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'}></th>
                                    <th scope={'col'}></th>
                                </tr>
                                </thead>
                                <tbody className={'table-group-divider'}>
                                {this.state.categories.content.map(category =>
                                    <CategoryInfoComponent key={category.id} category={category}
                                                           onUpdate={() => this.setState({category: category})}
                                                           deleteCategory={() => this.deleteCategory(category)}/>)}
                                </tbody>
                            </table>
                        </div>
                    </div>
                    : <div className={'d-flex justify-content-center align-items-center h-100'}>
                        <div className={'fs-3'}>Список категорий пуст</div>
                    </div>}
            </div>
            : <SpinnerComponent/>;
    }
}

class UserFormComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            role: {}, blocked: false
        };
    }

    componentDidMount() {
        axios.get(`/api/roles/all?sort=id,asc`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({roles: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.user !== this.props.user)
            this.setState(this.props.user ?
                {
                    role: this.props.user.role,
                    blocked: this.props.user.blocked
                } : {role: {}, blocked: false});
    }

    render() {
        return <Modal show={!!this.props.user} onHide={this.props.onClose}
                      backdrop={'static'} keyboard={false} centered>
            {this.props.user ? <>
                <Modal.Header closeButton>
                    <Modal.Title>
                        <div className={'modal-title fs-4'}>{this.props.user.name}</div>
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <form className={'has-validation'}>
                        {this.state.roles ?
                            <div>
                                <label htmlFor={'userRole'} className={'form-label fs-5'}>Роль</label>
                                <select name={'role'}
                                        value={this.state.role.id}
                                        onChange={e => this.setState({[e.target.name]: {id: parseInt(e.target.value)}})}
                                        id={'userRole'} className={'form-select'}>
                                    <option selected={!this.state.role.id} disabled={true}>
                                        ...
                                    </option>
                                    {this.state.roles.map(role =>
                                        <option key={role.id} value={role.id}>
                                            {role.name}
                                        </option>
                                    )}
                                </select>
                            </div> : null}
                        <div className={'form-check my-2'}>
                            <input type={'checkbox'} name={'blocked'}
                                   value={'true'} checked={this.state.blocked}
                                   onChange={e => this.setState({[e.target.name]: e.target.checked})}
                                   id={'userBlocked'} className={'form-check-input'}/>
                            <label htmlFor={'userBlocked'} className={'form-check-label fs-5'}>
                                Пользователь заблокирован
                            </label>
                        </div>
                    </form>
                </Modal.Body>
                <Modal.Footer>
                    <button className={'btn btn-primary w-100'}
                            onClick={e => this.props.editUser({
                                id: this.props.user.id,
                                blocked: this.state.blocked,
                                role: this.state.role
                            })}>
                        Изменить
                    </button>
                </Modal.Footer>
            </> : null}
        </Modal>;
    }
}

class UserInfoComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {role: props.user.role};
    }

    componentDidMount() {
        axios.get(`/api/roles/${this.state.role.id}`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({role: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    render() {
        return <tr>
            <td className={'fw-semibold'}>{this.props.user.id}</td>
            <td>{this.props.user.name}</td>
            <td>{this.props.user.email ? this.props.user.email : <span className={'null-table-cell'}></span>}</td>
            <td>{this.props.user.phone ? this.props.user.email : <span className={'null-table-cell'}></span>}</td>
            <td>{this.state.role.name}</td>
            <td>
                <button className={'btn btn-outline-primary'}
                        onClick={e => this.props.onEdit()}>
                    Изменить
                </button>
            </td>
        </tr>;
    }
}

class UserListComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            users: {number: 0, size: 20},
            sort: {attribute: 'id', order: 'asc'},
            filter: ''
        };
        this.loadUsers = this.loadUsers.bind(this);
        this.handleSort = this.handleSort.bind(this);
        this.editUser = this.editUser.bind(this);
    }

    loadUsers(page, sort) {
        axios.get(`/api/users?${this.state.filter && this.state.filter.length > 0 ? `filter=${this.state.filter}&` : ''}` +
            `page=${page ? page : this.state.users.number}&size=${this.state.users.size}` +
            `&sort=${sort ? sort.attribute : this.state.sort.attribute},${sort ? sort.order : this.state.sort.order}`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({users: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    handleSort(attribute) {
        let sort = {
            attribute: attribute,
            order: this.state.sort.attribute !== attribute || this.state.sort.order === 'desc' ? 'asc' : 'desc'
        };
        this.setState({sort: sort});
        this.loadUsers(false, sort);
    }

    editUser(user) {
        axios.put(`/api/users/edit/${user.id}`,
            JSON.stringify(user), {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            alert('Пользователь изменен!');
            this.setState({user: null});
            this.loadUsers();
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    componentDidMount() {
        this.loadUsers();
    }

    render() {
        return this.state.users.content ?
            <div className={'h-100'}>
                <UserFormComponent user={this.state.user}
                                   editUser={this.editUser}
                                   onClose={() => this.setState({user: null})}/>
                <div className={'p-3'}></div>
                <SearchComponent filter={this.state.filter}
                                 setFilter={filter => this.setState({filter: filter})}
                                 loadPage={this.loadUsers}/>
                {this.state.users.totalElements > 0 ?
                    <div className={'h-100'}>
                        <PaginationComponent page={this.state.users} loadPage={this.loadUsers}/>
                        <div className={'mx-5'}>
                            <table className={'table table-striped table-hover w-100'}>
                                <thead className={'table-light fw-bold'}>
                                <tr>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('id')}>
                                        ID{this.state.sort.attribute === 'id' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('name')}>
                                        Имя{this.state.sort.attribute === 'name' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('email')}>
                                        Почта{this.state.sort.attribute === 'email' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('phone')}>
                                        Телефон{this.state.sort.attribute === 'phone' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'} className={'pointer'}
                                        onClick={e => this.handleSort('role_id')}>
                                        Роль{this.state.sort.attribute === 'role_id' ? (this.state.sort.order === 'asc' ? ' ▲' : ' ▼') : null}
                                    </th>
                                    <th scope={'col'}></th>
                                </tr>
                                </thead>
                                <tbody className={'table-group-divider'}>
                                {this.state.users.content.map(user =>
                                    <UserInfoComponent key={user.id} user={user} roles={this.state.roles}
                                                       onEdit={() => this.setState({user: user})}/>)}
                                </tbody>
                            </table>
                        </div>
                    </div>
                    : <div className={'d-flex justify-content-center align-items-center h-100'}>
                        <div className={'fs-3'}>Список пользователей пуст</div>
                    </div>}
            </div>
            : <SpinnerComponent/>;
    }
}

class AdminComponent extends React.Component {
    constructor(props) {
        super(props);
        const params = new URLSearchParams(window.location.search);
        this.state = {tab: params.get('tab')};
    }

    componentDidMount() {
        authenticate(user => user.role.name.match('^ADMIN$') ?
            this.setState.bind(this)({user: user}) : location.href = '/error/403');
    }

    render() {
        return this.state.user && this.state.user.role.name.match('^ADMIN$') ?
            <div className={'row mx-0 h-100'}>
                <nav className={'col-3 nav flex-column border-bottom border-secondary rounded-start'}>
                    <a className={`admin-nav-link px-3 py-4 text-center fw-bold fs-6${this.state.tab === 'dishes' ? ' active' : ''}`}
                       href={'?tab=dishes'}>Блюда</a>
                    <a className={`admin-nav-link px-3 py-4 text-center fw-bold fs-6${this.state.tab === 'categories' ? ' active' : ''}`}
                       href={'?tab=categories'}>Категории блюд</a>
                    <a className={`admin-nav-link px-3 py-4 text-center fw-bold fs-6${this.state.tab === 'users' ? ' active' : ''}`}
                       href={'?tab=users'}>Пользователи</a>
                </nav>
                <div className={'col px-0 h-100'}>
                    {this.state.tab ?
                        <div className={'admin-content h-100'}>
                            {
                                {
                                    'dishes': <DishListComponent/>,
                                    'categories': <CategoryListComponent/>,
                                    'users': <UserListComponent/>
                                }[this.state.tab]
                            }
                        </div>
                        : <div className={'d-flex justify-content-center align-items-center h-100'}>
                            <div className={'text-white fs-4'}>Выберите вкладку для просмотра</div>
                        </div>}
                </div>
            </div>
            : <div className={'d-flex justify-content-center align-items-center h-100'}>
                <div className={'text-white fs-4'}>У вас недостаточно привелегий просматривать эту страницу</div>
            </div>;
    }
}

ReactDOM.createRoot(document.getElementById('page-container')).render(<PageComponent><AdminComponent/></PageComponent>);