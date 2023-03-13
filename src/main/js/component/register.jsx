import React from "react";
import ReactDOM from "react-dom/client";
import axios from "axios";
import {Collapse} from "react-bootstrap";
import {clearCookie} from "../authentication.js";
import "bootstrap/dist/css/bootstrap.min.css";

class RegisterComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            passwordCheck: false,
            login: '', password: '', matchingPsw: '', name: '', email: '', phone: '',
            invalidLogin: false, invalidPassword: false, noMatchingPsw: false,
            invalidName: false, invalidEmail: false, invalidPhone: false
        };
        this.handleRegister = this.handleRegister.bind(this);
    }

    handleRegister(e) {
        e.preventDefault();
        axios.post('/api/users/register',
            JSON.stringify({
                login: this.state.login,
                password: this.state.password,
                matchingPsw: this.state.matchingPsw,
                name: this.state.name,
                email: this.state.email
            }), {
                headers: {
                    'Authorization': null,
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            axios.post('/login',
                JSON.stringify({
                    login: this.state.login,
                    password: this.state.password
                }), {
                    headers: {
                        'Authorization': null,
                        'Accept': 'text/plain',
                        'Content-Type': 'application/json'
                    }
                }).then(result => {
                alert('Регистрация выполнена успешно!');
                clearCookie('cart');
                sessionStorage.setItem('token', result.data);
                location.href = '/';
            }).catch(error => {
                this.setState({error: 'Неправильный логин или пароль'});
                console.error('Error:', error.response ? error.response.data : error);
            })
        }).catch((error) => {
            console.error('Error:', error.response ? error.response.data : error);
        })
    }

    render() {
        return <div className={'register-container d-flex justify-content-center align-items-center vw-100 vh-100'}>
            <div className={'register p-4 w-25 rounded-3 shadow-2'}>
                <form className={'has-validation'}>
                    <div className={'form-floating my-1'}>
                        <input type={'text'} name={'login'} placeholder={'Введите логин'}
                               value={this.state.login}
                               pattern={'^[A-Za-z]\\w{5,20}$'}
                               title={'Должен начинаться с латинского символа и содержать не менее 5 символов'}
                               id={'login'} className={'form-control form-control-lg'}
                               onChange={e => this.setState({[e.target.name]: e.target.value})}/>
                        <label htmlFor={'login'} className={'form-label fs-5'}>Логин</label>
                        <div className={'invalid-feedback'}>Неверно указан логин</div>
                    </div>
                    <div className={'input-group my-1'}>
                        <div className={'form-floating'}>
                            <input type={this.state.showPassword ? 'text' : 'password'} name={'password'}
                                   placeholder={'Введите пароль'}
                                   value={this.state.password}
                                   pattern={'^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$'}
                                   title={'Должен содержать не менее одной цифры и одной прописной и строчной буквы, а также не менее 8 символов'}
                                   id={'password'} className={'form-control form-control-lg'}
                                   onFocus={e => this.setState({passwordCheck: true})}
                                   onBlur={e => this.setState({passwordCheck: false})}
                                   onChange={e => this.setState({[e.target.name]: e.target.value})}/>
                            <label htmlFor={'password'} className={'form-label fs-5'}>Пароль</label>
                            <div className={'invalid-feedback'}>Неверно указан пароль</div>
                        </div>
                        <button type={'button'} className={'show-password btn btn-outline-secondary'}
                                onMouseDown={e => this.setState({showPassword: true})}
                                onMouseUp={e => this.setState({showPassword: false})}
                                onMouseLeave={e => this.setState({showPassword: false})}>
                            👁
                        </button>
                    </div>
                    <Collapse in={this.state.passwordCheck} dimension={'width'}
                              className={'position-absolute'}>
                        <div className={'password-check-container card fs-5'}>
                            <div className={'password-check'}>
                                <div className={'p-4 fw-semibold'}>Пароль должен содержать:</div>
                                <div className={`p-3 ps-4 ${this.state.password.match('[a-z]') ? 'valid' : 'invalid'}`}>
                                    <b>Строчную</b> букву
                                </div>
                                <div className={`p-3 ps-4 ${this.state.password.match('[A-Z]') ? 'valid' : 'invalid'}`}>
                                    <b>Заглавную</b> букву
                                </div>
                                <div className={`p-3 ps-4 ${this.state.password.match('[0-9]') ? 'valid' : 'invalid'}`}>
                                    <b>Число</b>
                                </div>
                                <div className={`p-3 ps-4 ${this.state.password.length >= 8 ? 'valid' : 'invalid'}`}>
                                    Минимум <b>8 символов</b>
                                </div>
                            </div>
                        </div>
                    </Collapse>
                    <div className={'form-floating my-1'}>
                        <input type={'password'} name={'matchingPsw'}
                               placeholder={'Повторите пароль'}
                               value={this.state.matchingPsw}
                               pattern={`^${this.state.password}$`}
                               id={'matchingPsw'} className={'form-control form-control-lg'}
                               onChange={e => this.setState({[e.target.name]: e.target.value})}/>
                        <label htmlFor={'matchingPsw'} className={'form-label fs-5'}>Повторите пароль</label>
                        <div className={'invalid-feedback'}>Пароли должны совпадать</div>
                    </div>
                    <div className={'form-floating my-1'}>
                        <input type={'text'} name={'name'} placeholder={'Введите имя'}
                               value={this.state.name}
                               pattern={'^(\\p{L})+([. \'-](\\p{L})+)*$'}
                               title={'Не должно содержать цифр или специальных знаков'}
                               id={'name'} className={'form-control form-control-lg'}
                               onChange={e => this.setState({[e.target.name]: e.target.value})}/>
                        <label htmlFor={'name'} className={'form-label fs-5'}>Имя</label>
                        <div className={'invalid-feedback'}>Неверно указано имя</div>
                    </div>
                    <div className={'form-floating my-1'}>
                        <input type={'email'} name={'email'} placeholder={'Введите свою почту'}
                               value={this.state.email}
                               title={'Должен содержать символ @, а также не менее 8 символов'}
                               id={'email'} className={'form-control form-control-lg'}
                               onChange={e => this.setState({[e.target.name]: e.target.value})}/>
                        <label htmlFor={'email'} className={'form-label fs-5'}>Почта</label>
                        <div className={'invalid-feedback'}>Неверно указана почта</div>
                    </div>
                    <div className={'form-floating my-1'}>
                        <input type={'tel'} name={'phone'} placeholder={'Введите свой телефон'}
                               value={this.state.phone}
                               pattern={'^((\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- ]?\\d{3,4}[- ]?\\d{4})?$'}
                               title={'Должен быть введен номер телефона с кодом страны'}
                               id={'phone'} className={'form-control form-control-lg'}
                               onChange={e => this.setState({[e.target.name]: e.target.value})}/>
                        <label htmlFor={'phone'} className={'form-label fs-5'}>Телефон</label>
                        <div className={'invalid-feedback'}>Неверно указан телефон</div>
                    </div>
                    <div>
                        {this.state.error ?
                            <div className={'text-danger fw-bold fst-italic'}>{this.state.error}</div> : null}
                        <button className={'btn btn-success w-100 fs-5'} onClick={this.handleRegister}>
                            Зарегистрироваться
                        </button>
                    </div>
                </form>
                <div className={'d-flex justify-content-between mt-2'}>
                    <div>
                        <a className={'btn btn-outline-dark'} href={'/'}>Назад</a>
                    </div>
                    <div>
                        <div className={'text-end'}>
                            Уже есть аккаунт? <a href={'/login'}>Войти</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>;
    }
}

ReactDOM.createRoot(document.getElementById('register-container')).render(<RegisterComponent/>);