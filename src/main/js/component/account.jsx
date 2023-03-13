import React from "react";
import ReactDOM from "react-dom/client";
import axios from "axios";
import {PageComponent, PaginationComponent, SpinnerComponent} from "./page.jsx";
import {authenticate, forget} from "../authentication.js";
import "bootstrap/dist/css/bootstrap.min.css";
import {Accordion} from "react-bootstrap";

class ProfileComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: this.props.user.name,
            email: this.props.user.email ? this.props.user.email : '',
            phone: this.props.user.phone ? this.props.user.phone : '',
            setName: false, setEmail: false, setPhone: false
        };
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(e) {
        e.preventDefault();
        this.props.updateUser({
            name: this.state.name,
            email: this.state.email,
            phone: this.state.phone
        });
    }

    render() {
        return <div className={'d-flex justify-content-center align-items-center w-100 h-100'}>
            <div className={'card mt-4 w-50 shadow'}>
                <div className={'card-body'}>
                    <form className={'has-validation'}>
                        <table className={'table table-striped'}>
                            <tbody>
                            <tr>
                                <td>
                                    <label htmlFor={'login'} className={'form-label fs-5'}>Логин</label>
                                </td>
                                <td>
                                    <input type={'text'} name={'login'}
                                           value={this.props.user.login}
                                           disabled={true}
                                           id={'login'} className={'form-control form-control-plaintext ps-2'}/>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label htmlFor={'name'} className={'form-label fs-5'}>Имя</label>
                                </td>
                                <td>
                                    <div className={'input-group'}>
                                        <input type={'text'} name={'name'}
                                               value={this.state.name}
                                               pattern={'^(\\p{L})+([. \'-](\\p{L})+)*$'}
                                               title={'Не должно содержать цифр или специальных знаков'}
                                               onChange={e => this.setState({[e.target.name]: e.target.value})}
                                               id={'name'}
                                               disabled={!this.state.setName}
                                               className={`form-control${this.state.setName ? '' : ' form-control-plaintext ps-2'}`}/>
                                        <button type={'button'} className={'btn btn-outline-secondary'}
                                                onClick={e => this.setState({setName: !this.state.setName})}>
                                            Изменить имя
                                        </button>
                                    </div>
                                    <div className={'invalid-feedback'}>Неверно указано имя</div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label htmlFor={'email'} className={'form-label fs-5'}>Почта</label>
                                </td>
                                <td>
                                    <div className={'input-group'}>
                                        <input type={'email'} name={'email'}
                                               value={this.state.email}
                                               title={'Должен содержать символ @, а также не менее 8 символов'}
                                               onChange={e => this.setState({[e.target.name]: e.target.value})}
                                               id={'email'}
                                               disabled={!this.state.setEmail}
                                               className={`form-control${this.state.setEmail ? '' : ' form-control-plaintext ps-2'}`}/>
                                        <button type={'button'} className={'btn btn-outline-secondary'}
                                                onClick={e => this.setState({setEmail: !this.state.setEmail})}>
                                            Изменить почту
                                        </button>
                                    </div>
                                    <div className={'invalid-feedback'}>Неверно указана почта</div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label htmlFor={'phone'} className={'form-label fs-5'}>Телефон</label>
                                </td>
                                <td>
                                    <div className={'input-group'}>
                                        <input type={'tel'} name={'phone'}
                                               value={this.state.phone}
                                               pattern={'^((\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- ]?\\d{3,4}[- ]?\\d{4})?$'}
                                               title={'Должен быть введен номер телефона с кодом страны'}
                                               onChange={e => this.setState({[e.target.name]: e.target.value})}
                                               id={'phone'}
                                               disabled={!this.state.setPhone}
                                               className={`form-control${this.state.setPhone ? '' : ' form-control-plaintext ps-2'}`}/>
                                        <button type={'button'} className={'btn btn-outline-secondary'}
                                                onClick={e => this.setState({setPhone: !this.state.setPhone})}>
                                            Изменить телефон
                                        </button>
                                    </div>
                                    <div className={'invalid-feedback'}>Неверно указан телефон</div>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        <div>
                            <button
                                disabled={(this.state.name === '' || this.state.name === this.props.user.name)
                                    || (this.state.email === '' && this.state.email === this.props.user.email)
                                    || (this.state.phone === '' && this.state.phone === this.props.user.phone)}
                                className={'btn btn-secondary'}
                                onClick={this.handleSubmit}>Сохранить изменения
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>;
    }
}

class OrderInfoComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {address: props.order.address};
    }

    componentDidMount() {
        axios.get(`/api/addresses/${this.state.address.id}`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({address: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    render() {
        return <div className={'card'}>
            <div className={'card-header'}>
                <div className={'card-title fw-semibold'}>
                    Дата заказа: {new Date(Date.parse(this.props.order.orderDate)).toLocaleString()}
                </div>
                <div className={'card-text'}>
                    Указанное время: {new Date(Date.parse(this.props.order.specifiedDate)).toLocaleString()}
                </div>
            </div>
            <div className={'card-body'}>
                <div className={'col card-text fs-5 fw-semibold'}>
                    Итоговая стоимость: {this.props.order.price} р.
                </div>
                <Accordion>
                    <Accordion.Item eventKey={'0'}>
                        <Accordion.Header>Адрес</Accordion.Header>
                        <Accordion.Body>
                            {this.state.address ?
                                <div className={'form-control'}>
                                    <div className={'form-label'}>
                                        Страна: {this.state.address.country}
                                    </div>
                                    <div className={'form-label'}>
                                        Населенный пункт: {this.state.address.locality}
                                    </div>
                                    <div className={'form-label'}>
                                        Улица: {this.state.address.street}
                                    </div>
                                    <div className={'form-label'}>
                                        Дом: {this.state.address.house}
                                    </div>
                                    <div className={'form-label'}>
                                        Квартира: {this.state.address.apartment}
                                    </div>
                                </div> : null}
                        </Accordion.Body>
                    </Accordion.Item>
                </Accordion>
            </div>
            <div className={'card-footer'}>
                {this.props.tab === 'awaiting' ?
                    <div className={'d-flex justify-content-end w-100'}>
                        <button className={'btn btn-outline-danger btn-sm'}
                                onClick={e => this.props.cancelOrder()}>
                            Отмена заказа
                        </button>
                    </div> : null}
            </div>
        </div>;
    }
}

class OrderListComponent extends React.Component {
    constructor(props) {
        super(props);
        const params = new URLSearchParams(window.location.search);
        this.state = {
            orders: {number: 0, size: 10},
            tab: params.get('tab')
        };
        this.loadStatus = this.loadStatus.bind(this);
        this.loadOrders = this.loadOrders.bind(this);
        this.cancelOrder = this.cancelOrder.bind(this);
    }

    loadStatus() {
        axios.get(`/api/statuses/find?name=${this.state.tab.toUpperCase()}`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({status: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    loadOrders(page) {
        if (this.state.status)
            axios.get(`/api/orders?status=${this.state.status.id}&customer=${this.props.user.id}` +
                `&page=${page ? page : this.state.orders.number}&size=${this.state.orders.size}&sort=order_date,asc`, {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Accept': 'application/json'
                }
            }).then(result => {
                this.setState({orders: result.data});
            }).catch(error => {
                console.error('Error:', error.response ? error.response.data : error);
                if (error.response.status === 401)
                    location.href = '/error/401';
            })
    }

    cancelOrder(order) {
        axios.put(`/api/orders/cancel/${order.id}`, null, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null
            }
        }).then(result => {
            alert('Заказ отменен!');
            location.href = '';
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    componentDidMount() {
        if (this.state.tab) {
            if (!this.state.tab.match('^awaiting|preparing|ready|not_paid|finished$'))
                location.href = '/profile/orders';
            this.loadStatus();
        }
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (!this.state.orders.content) {
            this.loadOrders();
        }
    }

    render() {
        return <div className={'row mx-0 w-100 h-100'}>
            <nav className={'col-3 nav flex-column border-bottom border-secondary rounded-start'}>
                <a className={`order-nav-link px-3 py-4 text-center fw-bold fs-6${this.state.tab === 'awaiting' ? ' active' : ''}`}
                   href={'?tab=awaiting'}>В ожидании</a>
                <a className={`order-nav-link px-3 py-4 text-center fw-bold fs-6${this.state.tab === 'preparing' ? ' active' : ''}`}
                   href={'?tab=preparing'}>Готовятся</a>
                <a className={`order-nav-link px-3 py-4 text-center fw-bold fs-6${this.state.tab === 'ready' ? ' active' : ''}`}
                   href={'?tab=ready'}>Готовы</a>
                <a className={`order-nav-link px-3 py-4 text-center fw-bold fs-6${this.state.tab === 'not_paid' ? ' active' : ''}`}
                   href={'?tab=not_paid'}>Не оплачены</a>
                <a className={`order-nav-link px-3 py-4 text-center fw-bold fs-6${this.state.tab === 'finished$' ? ' active' : ''}`}
                   href={'?tab=finished'}>Доставлены</a>
            </nav>
            <div className={'col px-0 h-100 mt-4'}>
                {this.state.status ?
                    <div className={'h-100'}>
                        {this.state.orders.content ?
                            <div className={'h-100'}>
                                {this.state.orders.totalElements > 0 ?
                                    <div className={'h-100'}>
                                        <PaginationComponent page={this.state.orders}
                                                             loadPage={this.loadOrders}/>
                                        <table className={'table table-striped table-hover'}>
                                            <tbody>
                                            {this.state.orders.content.map(order =>
                                                <tr key={order.id}>
                                                    <td>
                                                        <OrderInfoComponent tab={this.state.tab} order={order}
                                                                            cancelOrder={() => this.cancelOrder(order)}/>
                                                    </td>
                                                </tr>)
                                            }
                                            </tbody>
                                        </table>
                                    </div>
                                    : <div className={'d-flex justify-content-center align-items-center pt-5 h-100'}>
                                        <div className={'text-white fs-3'}>История заказов пока пуста</div>
                                    </div>}
                            </div>
                            : <SpinnerComponent className={'text-white'}/>}
                    </div>
                    : <div className={'d-flex justify-content-center align-items-center pt-5 h-100'}>
                        <div className={'text-white fs-4'}>Выберите статус заказов для просмотра</div>
                    </div>}
            </div>
        </div>;
    }
}

class AccountComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {tab: window.location.pathname.substring('/account/'.length)};
        this.updateUser = this.updateUser.bind(this);
    }

    updateUser(user) {
        axios.put(`/api/users/update/${user.id}`,
            JSON.stringify(user), {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            forget(() => location.href = '');
            alert('Настройки изменены!');
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    componentDidMount() {
        if (!this.state.tab.match('^profile|orders$'))
            location.href = '/account/profile';
        authenticate(user => user.role.name.match('^CLIENT|MANAGER|ADMIN$') ?
            this.setState.bind(this)({user: user}) : location.href = '/error/403');
    }

    render() {
        return this.state.user && this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$') ?
            <div className={'h-100'}>
                <div className={'m-3 border-3 border-bottom text-start text-white fs-2 rounded-top'}>
                    Ваш аккаунт
                </div>
                <div className={'m-5'}>
                    <ul className={'nav-tab-header nav nav-tabs ps-2 pt-2'}>
                        <li>
                            <a href={'/account/profile'}
                               className={`nav-tab-link nav-link fw-semibold${this.state.tab === 'profile' ? ' active' : ''}`}>
                                Профиль
                            </a>
                        </li>
                        <li>
                            <a href={'/account/orders'}
                               className={`nav-tab-link nav-link fw-semibold${this.state.tab === 'orders' ? ' active' : ''}`}>
                                История заказов
                            </a>
                        </li>
                    </ul>
                    <div className={'d-flex justify-content-center h-100'}>
                        {
                            {
                                'profile': this.state.user ?
                                    <ProfileComponent user={this.state.user} updateUser={this.updateUser}/> : null,
                                'orders': this.state.user ?
                                    <OrderListComponent user={this.state.user}/> : null
                            }[this.state.tab]
                        }
                    </div>
                </div>
            </div>
            : <div className={'d-flex justify-content-center align-items-center h-100'}>
                <div className={'text-white fs-4'}>У вас недостаточно привелегий просматривать эту страницу</div>
            </div>;
    }
}

ReactDOM.createRoot(document.getElementById('page-container')).render(
    <PageComponent><AccountComponent/></PageComponent>);