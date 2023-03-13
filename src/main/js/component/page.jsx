import React from "react";
import {Dropdown} from "react-bootstrap";
import {authenticate, forget} from "../authentication.js";
import "bootstrap/dist/css/bootstrap.min.css";

class HeaderComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
        this.logout = this.logout.bind(this);
    }

    logout() {
        forget(() => location.href = '/');
    }

    componentDidMount() {
        authenticate(user => this.setState.bind(this)({user: user}));
    }

    render() {
        return <div
            className={'header-container d-flex justify-content-center position-fixed w-100' +
                `${this.props.scroll > 200 ? ' bg-opacity-75' : ''}`}>
            <div className={'header d-flex justify-content-between align-items-center h-100'}>
                <div className={'header-left d-flex justify-content-between align-items-center'}>
                    <nav className={'d-flex justify-content-around align-items-center w-100'}>
                        <div>
                            <a href={'/'} className={'nav-bar-link fw-bold'}>ГЛАВНАЯ</a>
                        </div>
                        <div>|</div>
                        <div>
                            <a href={'/menu'} className={'nav-bar-link fw-bold'}>МЕНЮ</a>
                        </div>
                        <div>|</div>
                        <div>
                            <a href={'/contacts'} className={'nav-bar-link fw-bold'}>КОНТАКТЫ</a>
                        </div>
                        <div>|</div>
                        <div>
                            <a href={'/about'} className={'nav-bar-link fw-bold'}>О НАС</a>
                        </div>
                    </nav>
                </div>
                <div>
                    <a href={'/'} className={'logo fs-3'}>Restaurant</a>
                </div>
                <div className={'header-right d-flex justify-content-evenly align-items-center float-end'}>
                    {this.state.user ? <>
                        <div className={'cart rounded-circle pointer'}>
                            <a href={'/cart'}>
                                <svg width={'35'} height={'35'} viewBox={'0 0 35 30.227'}>
                                    <g stroke={'#fff'} strokeWidth={2} fill={'none'} fillRule={'evenodd'}>
                                        <path
                                            d={'M14.318 26.25h6.364M1.591 1.591h3.713c.778 0 1.442.564 1.569 1.331l2.672 16.169-.723 3.182'}
                                            strokeLinecap={'round'} strokeLinejoin={'round'}/>
                                        <path
                                            d={'M7.954 6.364h24.659a.796.796 0 0 1 .764 1.016l-2.614 9.058a1.593 1.593 0 0 1-1.408 1.146L9.562 19.091m15.893 10.341a2.386 2.386 0 1 0-.001-4.775 2.386 2.386 0 0 0 .001 4.775Zm-1.125-4.773h2.25l1.26 1.262v2.25l-1.26 1.261h-2.25l-1.262-1.261v-2.25l1.262-1.262ZM9.545 29.432a2.387 2.387 0 1 0 0-4.774 2.387 2.387 0 0 0 0 4.774ZM8.42 24.659h2.25l1.262 1.262v2.25l-1.262 1.261H8.42l-1.26-1.261v-2.25l1.26-1.262Z'}/>
                                    </g>
                                </svg>
                            </a>
                        </div>
                        {this.state.user.role.name.match('^GUEST$') ?
                            <div className={'guest-actions d-flex justify-content-around'}>
                                <a href={'/register'} className={'btn btn-success'}>Регистрация</a>
                                <a href={'/login'} className={'btn btn-outline-success'}>Вход</a>
                            </div> : null}
                        {this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$') ?
                            <Dropdown>
                                <Dropdown.Toggle variant={'warning'}>
                                    <span className={'fs-5 fw-semibold'}>
                                        {this.state.user.name}
                                    </span>
                                </Dropdown.Toggle>
                                <Dropdown.Menu>
                                    <Dropdown.Item href={'/account'}>Личный кабинет</Dropdown.Item>
                                    {this.state.user.role.name.match('^MANAGER|ADMIN$') ?
                                        <Dropdown.Item href={'/managing'}>Заказы</Dropdown.Item> : null}
                                    {this.state.user.role.name.match('^ADMIN$') ?
                                        <Dropdown.Item href={'/admin'}>Администрация</Dropdown.Item> : null}
                                    <Dropdown.Item>
                                        <button className={'btn btn-outline-danger btn-sm'}
                                                onClick={e => this.logout()}>
                                            Выйти из аккаунта
                                        </button>
                                    </Dropdown.Item>
                                </Dropdown.Menu>
                            </Dropdown> : null}
                    </> : null}
                </div>
            </div>
        </div>;
    }
}

class FooterComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
        this.footer = React.createRef();
    }

    componentDidMount() {
        this.props.setOffset(this.footer.current.offsetTop);
    }

    render() {
        return <footer ref={this.footer}
                       className={'footer d-flex justify-content-center align-items-center position-absolute bottom-0 w-100'}>
            <div>©2022 LYSKOV KIRILL. RESTAURANT WEB PROJECT</div>
        </footer>;
    }
}

export class PageComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {scroll: 0};
        this.handleScroll = this.handleScroll.bind(this);
    }

    handleScroll() {
        this.setState({scroll: scrollY});
    }

    componentDidMount() {
        window.addEventListener('scroll', this.handleScroll);
    }

    componentWillUnmount() {
        window.removeEventListener('scroll', this.handleScroll);
    }

    render() {
        return <div className={'page-container d-flex justify-content-center position-relative min-vh-100'}>
            <HeaderComponent scroll={this.state.scroll}/>
            <div className={'content-wrap w-75'}>
                {this.props.children}
            </div>
            <div
                className={`scroll d-flex justify-content-center align-items-center rounded-circle pointer` +
                    `${this.state.scroll < 200 ? ' invisible opacity-0' : ' opacity-100'}` +
                    `${this.state.offset - 50 <= this.state.scroll && this.state.offset <= this.state.scroll + innerHeight ?
                        ' scroll-footer position-absolute' : ' position-fixed'}`}
                onClick={e => scroll({top: 0, behavior: 'smooth'})}>
                <svg width={25} height={25} viewBox={'-32 0 512 512'}>
                    <path fill={'white'}
                          d={'m34.9 289.5-22.2-22.2c-9.4-9.4-9.4-24.6 0-33.9L207 39c9.4-9.4 24.6-9.4 33.9 0l194.3 194.3c9.4 9.4 9.4 24.6 0 33.9L413 289.4c-9.5 9.5-25 9.3-34.3-.4L264 168.6V456c0 13.3-10.7 24-24 24h-32c-13.3 0-24-10.7-24-24V168.6L69.2 289.1c-9.3 9.8-24.8 10-34.3.4z'}/>
                </svg>
            </div>
            <FooterComponent setOffset={offset => this.setState({offset: offset})}/>
        </div>;
    }
}

export function PaginationComponent(props) {
    return <ul className={'pagination ms-4'}>
        {props.page.number > 1 ?
            <li className={'page-item'}>
                <button className={'page-link'}
                        onClick={e => props.loadPage(0)}>
                    1
                </button>
            </li> : null}
        {props.page.number > 2 ?
            <li className={'page-item'}>
                <button className={'page-link'} disabled={true}>
                    ...
                </button>
            </li> : null}
        {!props.page.first ?
            <li className={'page-item'}>
                <button className={'page-link'}
                        onClick={e => props.loadPage(props.page.number - 1)}>
                    {props.page.number}
                </button>
            </li> : null}
        <li className={'page-item active'}>
            <button className={'page-link'}
                    onClick={e => props.loadPage(props.page.number)}>
                {props.page.number + 1}
            </button>
        </li>
        {!props.page.last ?
            <li className={'page-item'}>
                <button className={'page-link'}
                        onClick={e => props.loadPage(props.page.number + 1)}>
                    {props.page.number + 2}
                </button>
            </li> : null}
        {props.page.number < props.page.totalPages - 3 ?
            <li className={'page-item'}>
                <button className={'page-link'} disabled={true}>
                    ...
                </button>
            </li> : null}
        {props.page.number < props.page.totalPages - 2 ?
            <li className={'page-item'}>
                <button className={'page-link'}
                        onClick={e => props.loadPage(props.page.totalPages - 1)}>
                    {props.page.totalPages}
                </button>
            </li> : null}
    </ul>;
}

export function SearchComponent(props) {
    return <div className={'d-flex justify-content-center my-2'}>
        <div className={'input-group rounded-pill w-25'}>
            <input type={'search'} placeholder={'Введите запрос...'}
                   value={props.filter}
                   className={'form-control'}
                   onChange={e => props.setFilter(e.target.value)}/>
            <button className={'btn btn-primary'} onClick={e => props.loadPage(0)}>
                <svg width={25} height={25} viewBox={'0 0 24 24'}>
                    <path fill={'white'} stroke={'2'}
                          d={'m20.87 20.17-5.59-5.59A6.956 6.956 0 0 0 17 10c0-3.87-3.13-7-7-7s-7 3.13-7 7a6.995 6.995 0 0 0 11.58 5.29l5.59 5.59.7-.71zM10 16c-3.31 0-6-2.69-6-6s2.69-6 6-6 6 2.69 6 6-2.69 6-6 6z'}/>
                </svg>
            </button>
        </div>
    </div>;
}

export function SpinnerComponent(props) {
    return <div className={'d-flex justify-content-center align-items-center h-100'}>
        <div className={`spinner-border spinner-lg ${props.className ? props.className : ''}`}></div>
    </div>;
}