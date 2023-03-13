import React from "react";
import ReactDOM from "react-dom/client";
import axios from "axios";
import {PageComponent, PaginationComponent, SpinnerComponent} from "./page.jsx";
import {authenticate} from "../authentication.js";
import "bootstrap/dist/css/bootstrap.min.css";
import {Accordion} from "react-bootstrap";

class OrderInfoComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            customer: props.order.customer,
            manager: props.order.manager ? props.order.manager : {},
            address: props.order.address
        };
    }

    componentDidMount() {
        axios.get(`/api/users/${this.state.customer.id}`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({customer: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
        if (this.state.manager.id)
            axios.get(`/api/users/${this.state.manager.id}`, {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Accept': 'application/json'
                }
            }).then(result => {
                this.setState({manager: result.data});
            }).catch(error => {
                console.error('Error:', error.response ? error.response.data : error);
                if (error.response.status === 401)
                    location.href = '/error/401';
            })
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
                {this.props.tab === 'finished' ?
                    <div className={'card-text'}>
                        Время доставки: {new Date(Date.parse(this.props.order.deliveryDate)).toLocaleString()}
                    </div> : null}
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
                        <button className={'btn btn-primary btn-sm'}
                                onClick={e => this.props.updateOrder({
                                    id: this.props.order.id,
                                    status: this.props.statusPreparing,
                                    manager: {id: this.props.user.id}
                                })}>
                            Принять
                        </button>
                    </div> : null}
                {this.props.tab === 'preparing' ?
                    <div className={'d-flex justify-content-end w-100'}>
                        <button className={'btn btn-primary btn-sm'}
                                onClick={e => this.props.updateOrder({
                                    id: this.props.order.id,
                                    status: this.props.statusReady,
                                })}>
                            Готов
                        </button>
                    </div> : null}
                {this.props.tab === 'ready' ?
                    <div className={'d-flex justify-content-end w-100'}>
                        <button className={'btn btn-success btn-sm me-3'}
                                onClick={e => this.props.updateOrder({
                                    id: this.props.order.id,
                                    status: this.props.statusFinished
                                })}>
                            Доставлен
                        </button>
                        <button className={'btn btn-outline-warning btn-sm ms-3'}
                                onClick={e => this.props.updateOrder({
                                    id: this.props.order.id,
                                    status: this.props.statusNotPaid
                                })}>
                            Не оплачен
                        </button>
                    </div> : null}
            </div>
        </div>;
    }
}

class ManagingComponent extends React.Component {
    constructor(props) {
        super(props);
        const params = new URLSearchParams(window.location.search);
        this.state = {
            orders: {number: 0, size: 10},
            tab: params.get('tab')
        };
        this.loadStatus = this.loadStatus.bind(this);
        this.loadOrders = this.loadOrders.bind(this);
        this.updateOrder = this.updateOrder.bind(this);
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
            axios.get(`/api/orders?status=${this.state.status.id}&page=${page ? page : this.state.orders.number}&size=${this.state.orders.size}&sort=order_date,asc`, {
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

    updateOrder(order) {
        axios.put(`/api/orders/update/${order.id}`,
            JSON.stringify(order), {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            alert('Статус заказа обновлен!');
            this.loadOrders();
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    componentDidMount() {
        if (this.state.tab && !this.state.tab.match('^awaiting|preparing|ready|not_paid|finished$'))
            location.href = '/managing';
        authenticate(user => {
            if (user.role.name.match('^MANAGER|ADMIN$')) {
                this.setState.bind(this)({user: user});
            } else location.href = '/error/403';
            if (this.state.tab) {
                this.loadStatus();
                if (this.state.tab === 'awaiting') {
                    axios.get(`/api/statuses/find?name=PREPARING`, {
                        headers: {
                            'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                            'Accept': 'application/json'
                        }
                    }).then(result => {
                        this.setState({statusPreparing: result.data});
                    }).catch(error => {
                        console.error('Error:', error.response ? error.response.data : error);
                        if (error.response.status === 401)
                            location.href = '/error/401';
                    })
                }
                if (this.state.tab === 'preparing') {
                    axios.get(`/api/statuses/find?name=READY`, {
                        headers: {
                            'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                            'Accept': 'application/json'
                        }
                    }).then(result => {
                        this.setState({statusReady: result.data});
                    }).catch(error => {
                        console.error('Error:', error.response ? error.response.data : error);
                        if (error.response.status === 401)
                            location.href = '/error/401';
                    })
                }
                if (this.state.tab === 'ready') {
                    axios.get(`/api/statuses/find?name=FINISHED`, {
                        headers: {
                            'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                            'Accept': 'application/json'
                        }
                    }).then(result => {
                        this.setState({statusFinished: result.data});
                    }).catch(error => {
                        console.error('Error:', error.response ? error.response.data : error);
                        if (error.response.status === 401)
                            location.href = '/error/401';
                    })
                    axios.get(`/api/statuses/find?name=NOT_PAID`, {
                        headers: {
                            'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                            'Accept': 'application/json'
                        }
                    }).then(result => {
                        this.setState({statusNotPaid: result.data});
                    }).catch(error => {
                        console.error('Error:', error.response ? error.response.data : error);
                        if (error.response.status === 401)
                            location.href = '/error/401';
                    })
                }
            }
        });
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (!this.state.orders.content && this.state.user) {
            this.loadOrders();
        }
    }

    render() {
        return this.state.user && this.state.user.role.name.match('^MANAGER|ADMIN$') ?
            <div className={'row mx-0 h-100'}>
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
                <div className={'col px-0 h-100'}>
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
                                                                                user={this.state.user}
                                                                                statusPreparing={this.state.statusPreparing}
                                                                                statusReady={this.state.statusReady}
                                                                                statusFinished={this.state.statusFinished}
                                                                                statusNotPaid={this.state.statusNotPaid}
                                                                                updateOrder={order => this.updateOrder(order)}/>
                                                        </td>
                                                    </tr>)
                                                }
                                                </tbody>
                                            </table>
                                        </div>
                                        : <div className={'d-flex justify-content-center align-items-center h-100'}>
                                            <div className={'text-white fs-3'}>Список заказов пуст</div>
                                        </div>}
                                </div>
                                : <SpinnerComponent className={'text-white'}/>}
                        </div>
                        : <div className={'d-flex justify-content-center align-items-center h-100'}>
                            <div className={'text-white fs-4'}>Выберите статус заказов для просмотра</div>
                        </div>}
                </div>
            </div>
            : <div className={'d-flex justify-content-center align-items-center h-100'}>
                <div className={'text-white fs-4'}>У вас недостаточно привелегий просматривать эту страницу</div>
            </div>;
    }
}

ReactDOM.createRoot(document.getElementById('page-container')).render(
    <PageComponent><ManagingComponent/></PageComponent>);