import React from "react";
import ReactDOM from "react-dom/client";
import axios from "axios";
import {Collapse, Modal} from "react-bootstrap";
import {PageComponent, PaginationComponent, SpinnerComponent} from "./page.jsx";
import {authenticate} from "../authentication.js";
import "bootstrap/dist/css/bootstrap.min.css";

class OrderFormComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showAddress: false,
            country: '', locality: '', street: '', house: '', apartment: ''
        };
        this.loadAddresses = this.loadAddresses.bind(this);
        this.addAddress = this.addAddress.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    loadAddresses() {
        axios.get(`/api/addresses?user=${this.props.order.customer.id}&sort=id,asc`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({addresses: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    addAddress(e) {
        e.preventDefault();
        axios.post('/api/addresses/add',
            JSON.stringify({
                country: this.state.country,
                locality: this.state.locality,
                street: this.state.street,
                house: this.state.house,
                apartment: this.state.apartment,
                user: {id: this.props.order.customer.id}
            }), {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            alert('Адрес добавлен!');
            this.setState({
                showAddress: false,
                country: '', locality: '', street: '', house: '', apartment: ''
            });
            this.loadAddresses();
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    handleSubmit(e) {
        e.preventDefault();
        if (this.props.order) {
            let now = Date.now();
            let date = Date.parse(this.state.specifiedDate);
            date = Math.max(date, now + 30 * 60);
            date = Math.min(date, now + 7 * 24 * 60 * 60);
            this.props.confirmOrder({
                id: this.props.order.id,
                price: this.props.total,
                specifiedDate: new Date(date),
                address: this.state.address
            });
        }
    }

    componentDidMount() {
        axios.get('/api/statuses/find?name=AWAITING', {
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

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.order !== this.props.order) {
            this.setState({
                specifiedDate: null,
                address: {},
                showAddress: false,
                country: '', locality: '', street: '', house: '', apartment: ''
            });
            if (this.props.order)
                this.loadAddresses();
        }
    }

    render() {
        return <Modal show={!!this.props.order} onHide={this.props.onClose}
                      backdrop={'static'} keyboard={false} centered>
            {this.props.order ? <>
                <Modal.Header closeButton>
                    <Modal.Title>
                        <div className={'modal-title fs-4'}>
                            Оформление заказа
                        </div>
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <form className={'has-validation'}>
                        <div className={'my-1'}>
                            {this.state.addresses && this.state.address ?
                                <div className={'my-2'}>
                                    <label htmlFor={'address'} className={'form-label'}>Адрес</label>
                                    <select name={'address'}
                                            value={this.state.address.id}
                                            onChange={e => this.setState({[e.target.name]: {id: parseInt(e.target.value)}})}
                                            id={'address'} className={'form-select'}>
                                        <option selected={!this.state.address.id} disabled={true}>
                                            ...
                                        </option>
                                        {this.state.addresses.map(address =>
                                            <option key={address.id} value={address.id}>
                                                {address.locality}, {address.country}, {address.street} {address.house} {address.apartment}
                                            </option>
                                        )}
                                    </select>
                                </div> : null}
                            <div className={'w-100'}>
                                <button className={'btn btn-outline-primary'}
                                        onClick={e => {
                                            e.preventDefault();
                                            this.setState({showAddress: !this.state.showAddress});
                                        }}>
                                    Добавить новый адрес
                                </button>
                                <Collapse in={this.state.showAddress}>
                                    <div className={'card my-2 px-2'}>
                                        <div>
                                            <label htmlFor={'country'} className={'form-label'}>
                                                Страна
                                            </label>
                                            <input type={'text'} name={'country'}
                                                   value={this.state.country}
                                                   onChange={e => this.setState({[e.target.name]: e.target.value})}
                                                   id={'country'} className={'form-control'}/>
                                        </div>
                                        <div>
                                            <label htmlFor={'locality'} className={'form-label'}>
                                                Населенный пункт
                                            </label>
                                            <input type={'text'} name={'locality'}
                                                   value={this.state.locality}
                                                   onChange={e => this.setState({[e.target.name]: e.target.value})}
                                                   id={'locality'} className={'form-control'}/>
                                        </div>
                                        <div>
                                            <label htmlFor={'street'} className={'form-label'}>
                                                Улица
                                            </label>
                                            <input type={'text'} name={'street'}
                                                   value={this.state.street}
                                                   onChange={e => this.setState({[e.target.name]: e.target.value})}
                                                   id={'street'} className={'form-control'}/>
                                        </div>
                                        <div>
                                            <label htmlFor={'house'} className={'form-label'}>
                                                Дом
                                            </label>
                                            <input type={'text'} name={'house'}
                                                   value={this.state.house}
                                                   onChange={e => this.setState({[e.target.name]: e.target.value})}
                                                   id={'house'} className={'form-control'}/>
                                        </div>
                                        <div>
                                            <label htmlFor={'apartment'} className={'form-label'}>
                                                Квартира
                                            </label>
                                            <input type={'text'} name={'apartment'}
                                                   value={this.state.apartment}
                                                   onChange={e => this.setState({[e.target.name]: e.target.value})}
                                                   id={'apartment'} className={'form-control'}/>
                                        </div>
                                        <div className={'d-flex justify-content-center my-1 w-100'}>
                                            <button className={'btn btn-primary btn-sm'}
                                                    onClick={this.addAddress}>
                                                Добавить адрес
                                            </button>
                                        </div>
                                    </div>
                                </Collapse>
                            </div>
                        </div>
                        <div>
                            <label htmlFor={'specifiedDate'} className={'form-label fs-5'}>Время доставки</label>
                            <input type={'datetime-local'} name={'specifiedDate'} placeholder={'Время доставки'}
                                   value={this.state.specifiedDate}
                                   title={'Минимальная дата - текущая + пол часа, максимальная - текущая + 7 дней'}
                                   id={'specifiedDate'} className={'form-control'}
                                   onChange={e => this.setState({specifiedDate: e.target.value})}/>
                        </div>
                    </form>
                    <div className={'mt-3 px-3 bg-light fs-4'}>
                        Итоговая сумма: {this.props.total} р.
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <button className={'btn btn-success w-100'}
                            onClick={this.handleSubmit}>
                        Подтвердить заказ
                    </button>
                </Modal.Footer>
            </> : null}
        </Modal>;
    }
}

class OrderItemFormComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {quantity: props.orderItem.quantity, dish: props.orderItem.dish};
        this.handleQuantity = this.handleQuantity.bind(this);
    }

    handleQuantity(quantity) {
        if ((quantity = parseInt(quantity))) {
            if (quantity !== this.props.orderItem.quantity) {
                this.props.editOrderItem({
                    id: this.props.orderItem.id,
                    quantity: quantity,
                    dish: this.props.orderItem.dish
                });
            }
        } else
            this.setState({quantity: this.props.orderItem.quantity});
    }

    componentDidMount() {
        axios.get(`/api/dishes/${this.state.dish.id}`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({dish: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    render() {
        return <div className={'card'}>
            <div className={'d-flex'}>
                <div className={'dish-image p-1'}>
                    <img src={this.state.dish.imageUrl} alt={'Image'}
                         className={'position-relative w-100 h-100 rounded'}/>
                </div>
                <div className={'w-100'}>
                    <div className={'card-header'}>
                        <div className={'card-title fs-5 fw-semibold'}>{this.state.dish.name}</div>
                    </div>
                    <div className={'card-body'}>
                        <div className={'card-title fs-5 fw-bold'}>{this.state.dish.price} р.</div>
                    </div>
                    <div className={'card-footer'}>
                        <div
                            className={'order-item-quantity input-group px-2 w-50 rounded-pill border border-secondary'}>
                            <button className={'order-item-minus btn btn-close px-2 py-3 '}
                                    onClick={e => this.handleQuantity(this.state.quantity - 1)}></button>
                            <input type={'number'} placeholder={'Количество'}
                                   value={this.state.quantity}
                                   className={'form-control fs-5'}
                                   onChange={e => this.setState({quantity: e.target.value})}
                                   onKeyUp={e => {
                                       if (e.key === 'Enter')
                                           this.handleQuantity(e.target.value);
                                   }}
                                   onBlur={e => this.handleQuantity(e.target.value)}/>
                            <button className={'order-item-plus btn btn-close px-2 py-3'}
                                    onClick={e => this.handleQuantity(this.state.quantity + 1)}></button>
                            <button className={'btn btn-close py-3 border-start border-secondary'}
                                    onClick={e => this.handleQuantity(0)}></button>
                        </div>
                    </div>
                </div>
            </div>
        </div>;
    }
}

class CartComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {orderItems: {number: 0, size: 10}};
        this.loadOrderItems = this.loadOrderItems.bind(this);
        this.loadOrderItemsTotal = this.loadOrderItemsTotal.bind(this);
        this.editOrderItem = this.editOrderItem.bind(this);
        this.confirmOrder = this.confirmOrder.bind(this);
    }

    loadOrderItems(page) {
        axios.get(`/api${!this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$') ? '/cookie/cart?'
                : `/order-items?${this.state.user.order ? `order=${this.state.user.order.id}&` : ''}`}` +
            `page=${page ? page : this.state.orderItems.number}&size=${this.state.orderItems.size}&sort=id,asc`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({orderItems: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    loadOrderItemsTotal() {
        if (this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$'))
            axios.get(`/api/order-items/total?${this.state.user.order ? `order=${this.state.user.order.id}` : ''}`, {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Accept': 'application/json'
                }
            }).then(result => {
                this.setState({total: result.data});
            }).catch(error => {
                console.error('Error:', error.response ? error.response.data : error);
                if (error.response.status === 401)
                    location.href = '/error/401';
            })
    }

    editOrderItem(orderItem) {
        if (orderItem.quantity > 0) {
            axios.put(`/api${!this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$') ? '/cookie/cart' : '/order-items'}/update/` +
                `${this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$') ? orderItem.id : orderItem.dish.id}`,
                JSON.stringify(orderItem), {
                    headers: {
                        'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                        'Content-Type': 'application/json'
                    }
                }).then(result => {
                this.loadOrderItems();
                this.loadOrderItemsTotal();
            }).catch(error => {
                console.error('Error:', error.response ? error.response.data : error);
                if (error.response.status === 401)
                    location.href = '/error/401';
            })
        } else {
            axios.delete(`/api${!this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$') ? '/cookie/cart' : '/order-items'}/delete/` +
                `${this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$') ? orderItem.id : orderItem.dish.id}`, {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null
                }
            }).then(result => {
                this.loadOrderItems();
                this.loadOrderItemsTotal();
            }).catch(error => {
                console.error('Error:', error.response ? error.response.data : error);
                if (error.response.status === 401)
                    location.href = '/error/401';
            })
        }
    }

    confirmOrder(order) {
        axios.put(`/api/orders/confirm/${order.id}`,
            JSON.stringify(order), {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            alert('Заказ подтвержден!');
            location.href = '';
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    componentDidMount() {
        authenticate(user => {
            this.setState.bind(this)({user: user});
            if (user.role.name.match('^CLIENT|MANAGER|ADMIN$'))
                axios.get(`/api/orders/${user.order.id}`, {
                    headers: {
                        'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                        'Accept': 'application/json'
                    }
                }).then(result => {
                    this.setState({cart: result.data});
                }).catch(error => {
                    console.error('Error:', error.response ? error.response.data : error);
                    if (error.response.status === 401)
                        location.href = '/error/401';
                })
        });
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (!this.state.orderItems.content && this.state.user) {
            this.loadOrderItems();
            this.loadOrderItemsTotal();
        }
    }

    render() {
        return <div className={'h-100'}>
            <div className={'m-3 border-3 border-bottom text-center text-white fs-2'}>
                Корзина
            </div>
            {this.state.orderItems.content ?
                <div className={'h-100'}>
                    {this.state.orderItems.totalElements > 0 ?
                        <div>
                            <OrderFormComponent order={this.state.order} total={this.state.total}
                                                confirmOrder={this.confirmOrder}
                                                onClose={() => this.setState({order: null})}/>
                            <PaginationComponent page={this.state.orderItems} loadPage={this.loadOrderItems}/>
                            <div>
                                <div className={'d-flex justify-content-center'}>
                                    <table className={'table table-borderless w-75'}>
                                        <tbody>{this.state.orderItems.content.map(orderItem =>
                                            <tr key={orderItem.id}>
                                                <td>
                                                    <OrderItemFormComponent orderItem={orderItem}
                                                                            editOrderItem={this.editOrderItem}/>
                                                </td>
                                            </tr>)}
                                        </tbody>
                                    </table>
                                </div>
                                <div className={'d-flex justify-content-center m-4'}>
                                    <button className={'btn btn-success btn-lg w-50'}
                                            onClick={e => {
                                                if (this.state.user && this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$'))
                                                    this.setState({order: this.state.cart})
                                                else alert('Вы не сможете оформить заказ пока не зарегистрируетесь');
                                            }}>
                                        Оформить заказ
                                    </button>
                                </div>
                            </div>
                        </div>
                        : <div className={'d-flex justify-content-center align-items-center h-100'}>
                            <div className={'text-center'}>
                                <div className={'fs-3 text-white'}>В корзине пока пусто</div>
                                <a className={'btn btn-danger my-2'} href={'/menu'}>Посмотреть меню</a>
                            </div>
                        </div>}
                </div>
                : <SpinnerComponent className={'text-white'}/>}
        </div>;
    }
}

ReactDOM.createRoot(document.getElementById('page-container')).render(<PageComponent><CartComponent/></PageComponent>);