import React from "react";
import ReactDOM from "react-dom/client";
import axios from "axios";
import {clearCookie} from "../authentication.js";
import "bootstrap/dist/css/bootstrap.min.css";

class LoginComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {login: '', password: '', remember: false};
        this.handleLogin = this.handleLogin.bind(this);
    }

    handleLogin(e) {
        e.preventDefault();
        axios.post('/login',
            JSON.stringify({
                login: this.state.login,
                password: this.state.password
            }), {
                params: this.state.remember ? {
                    'remember-me': 'on'
                } : null,
                headers: {
                    'Authorization': null,
                    'Accept': 'text/plain',
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            clearCookie('cart');
            sessionStorage.setItem('token', result.data);
            window.location = '/';
        }).catch(error => {
            this.setState({error: 'Неправильный логин или пароль'});
            console.error('Error:', error.response ? error.response.data : error);
        })
    }

    render() {
        return <div className={'login-container d-flex justify-content-center align-items-center vw-100 vh-100'}>
            <div className={'login p-4 w-25 rounded-3 shadow-2'}>
                <form className={'has-validation'}>
                    <div className={'my-1'}>
                        <label htmlFor={'login'} className={'form-label fs-5'}>Логин</label>
                        <input type={'text'} name={'login'} placeholder={'Введите логин'}
                               value={this.state.login} autoComplete={'false'}
                               pattern={'^[A-Za-z]\\w{5,20}$'}
                               title={'Должен начинаться с латинского символа и содержать не менее 5 символов'}
                               id={'login'} className={'form-control form-control-lg'}
                               onChange={e => this.setState({[e.target.name]: e.target.value})}/>
                        <div className={'invalid-feedback'}>Неверно указан логин</div>
                    </div>
                    <div className={'my-1'}>
                        <label htmlFor={'password'} className={'form-label fs-5'}>Пароль</label>
                        <div className={'input-group'}>
                            <input type={this.state.showPassword ? 'text' : 'password'} name={'password'}
                                   placeholder={'Введите пароль'}
                                   value={this.state.password} autoComplete={'false'}
                                   pattern={'^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$'}
                                   title={'Должен содержать не менее одной цифры и одной прописной и строчной буквы, а также не менее 8 символов'}
                                   id={'password'} className={'form-control form-control-lg'}
                                   onChange={e => this.setState({[e.target.name]: e.target.value})}/>
                            <button type={'button'} className={'show-password btn btn-outline-secondary'}
                                    onMouseDown={e => this.setState({showPassword: true})}
                                    onMouseUp={e => this.setState({showPassword: false})}
                                    onMouseLeave={e => this.setState({showPassword: false})}>
                                👁
                            </button>
                        </div>
                        <div className={'invalid-feedback'}>Неверно указан пароль</div>
                    </div>
                    <div className={'form-check my-2 border-top border-secondary'}>
                        <input type={'checkbox'} name={'remember'}
                               checked={this.state.remember}
                               id={'remember'} className={'form-check-input'}
                               onChange={e => this.setState({[e.target.name]: e.target.checked})}/>
                        <label htmlFor={'remember'} className={'form-check-label fs-6'}>
                            Запомнить меня
                        </label>
                    </div>
                    <div>
                        {this.state.error ?
                            <div className={'text-danger fw-bold fst-italic'}>{this.state.error}</div> : null}
                        <button className={'btn btn-primary w-100 fs-5'} onClick={this.handleLogin}>
                            Войти
                        </button>
                    </div>
                </form>
                <div className={'d-flex justify-content-between mt-2'}>
                    <div>
                        <a className={'btn btn-outline-dark'} href={'/'}>Назад</a>
                    </div>
                    <div>
                        <div className={'text-end'}>
                            Забыли <a href={'/recover'}>пароль?</a>
                        </div>
                        <div className={'text-end'}>
                            <a href={'/register'}>Зарегистрироваться</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>;
    }
}

ReactDOM.createRoot(document.getElementById('login-container')).render(<LoginComponent/>);