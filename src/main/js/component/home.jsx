import React from "react";
import ReactDOM from "react-dom/client";
import axios from "axios";
import {Carousel} from "react-bootstrap";
import {PageComponent, PaginationComponent, SpinnerComponent} from "./page.jsx";
import {authenticate} from "../authentication.js";
import "bootstrap/dist/css/bootstrap.min.css";

class ReviewFormComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {review: false, temp: 0, grade: 0, comment: ''};
    }

    handleSubmit(e) {
        e.preventDefault();
        this.props.addReview({
            grade: this.state.grade,
            comment: this.state.comment,
            user: this.state.user
        });
        this.setState({review: false, grade: 0, comment: ''});
    }

    componentDidMount() {
        authenticate(user => this.setState.bind(this)({user: user}));
    }

    render() {
        return this.state.user && this.state.user.role.name.match('^CLIENT|MANAGER|ADMIN$') ?
            <div className={'my-5'}>
                {this.state.review ?
                    <form className={'mx-5 rounded bg-light'}>
                        <div className={'review-grade m-2 fs-4'}>
                            <span
                                className={`star pointer${this.state.temp > 0 || this.state.grade > 0 ? ' active' : ''}`}
                                onClick={e => this.setState({temp: 1, grade: 1})}
                                onMouseEnter={e => this.setState({temp: 1})}
                                onMouseLeave={e => this.setState({temp: this.state.grade})}>★</span>
                            <span
                                className={`star pointer${this.state.temp > 1 || this.state.grade > 1 ? ' active' : ''}`}
                                onClick={e => this.setState({temp: 2, grade: 2})}
                                onMouseEnter={e => this.setState({temp: 2})}
                                onMouseLeave={e => this.setState({temp: this.state.grade})}>★</span>
                            <span
                                className={`star pointer${this.state.temp > 2 || this.state.grade > 2 ? ' active' : ''}`}
                                onClick={e => this.setState({temp: 3, grade: 3})}
                                onMouseEnter={e => this.setState({temp: 3})}
                                onMouseLeave={e => this.setState({temp: this.state.grade})}>★</span>
                            <span
                                className={`star pointer${this.state.temp > 3 || this.state.grade > 3 ? ' active' : ''}`}
                                onClick={e => this.setState({temp: 4, grade: 4})}
                                onMouseEnter={e => this.setState({temp: 4})}
                                onMouseLeave={e => this.setState({temp: this.state.grade})}>★</span>
                            <span
                                className={`star pointer${this.state.temp > 4 || this.state.grade > 4 ? ' active' : ''}`}
                                onClick={e => this.setState({temp: 5, grade: 5})}
                                onMouseEnter={e => this.setState({temp: 5})}
                                onMouseLeave={e => this.setState({temp: this.state.grade})}>★</span>
                        </div>
                        <div className={'d-flex justify-content-center px-4'}>
                            <textarea value={this.state.comment}
                                      className={'form-control form-control-lg'}
                                      onChange={e => this.setState({comment: e.target.value})}>
                            </textarea>
                        </div>
                        <div className={'d-flex justify-content-end py-3'}>
                            <button disabled={this.state.grade === 0 || this.state.comment.length === 0}
                                    className={'btn btn-success mx-3'}
                                    onClick={this.handleSubmit}>
                                Отправить
                            </button>
                            <button className={'btn btn-outline-dark mx-3'}
                                    onClick={e => {
                                        e.preventDefault();
                                        this.setState({review: false, grade: 0, comment: ''});
                                    }}>
                                Отменить
                            </button>
                        </div>
                    </form>
                    : <div className={'mx-3'}>
                        <button className={'btn btn-danger'}
                                onClick={e => this.setState({review: true})}>
                            Оставить отзыв
                        </button>
                    </div>}
            </div> : null;
    }
}

class ReviewInfoComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    componentDidMount() {
        axios.get(`/api/users/${this.props.review.user.id}`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({user: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    render() {
        return <div className={'card-head d-flex justify-content-start p-1 bg-light rounded-top'}>
            <div
                className={'review-user card-title fs-7 fw-semibold'}>{this.state.user ? this.state.user.name : '?'}</div>
            <div className={'mx-2'}>{Array.from(Array(5), (_, i) =>
                <span key={i} className={`star ${i < this.props.review.grade ? 'active' : ''}`}>★</span>)}</div>
            <div className={'text-secondary'}>{new Date(Date.parse(this.props.review.date)).toLocaleString()}</div>
        </div>;
    }
}

class ReviewListComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {reviews: {number: 0, size: 10}};
        this.loadReviews = this.loadReviews.bind(this);
        this.addReview = this.addReview.bind(this);
    }

    loadReviews(page) {
        axios.get(`/api/reviews?page=${page ? page : this.state.reviews.number}&size=${this.state.reviews.size}&sort=date,asc`, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            this.setState({reviews: result.data});
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    addReview(review) {
        axios.post('/api/reviews/add',
            JSON.stringify(review), {
                headers: {
                    'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                    'Content-Type': 'application/json'
                }
            }).then(result => {
            alert('Отзыв добавлен!');
            this.loadReviews();
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401)
                location.href = '/error/401';
        })
    }

    componentDidMount() {
        this.loadReviews();
    }

    render() {
        return <div className={'my-5 mx-4'}>
            <div className={'my-3 border-3 border-top text-white fs-2'}>Отзывы</div>
            <div>
                {this.state.reviews.content ?
                    <div>
                        {this.state.reviews.totalElements > 0 ?
                            <div>
                                <PaginationComponent page={this.state.reviews} loadPage={this.loadReviews}/>
                                <table className={'table table-borderless'}>
                                    <tbody>
                                    {this.state.reviews.content.map(review =>
                                        <tr key={review.id}>
                                            <td>
                                                <div className={'card'}>
                                                    <ReviewInfoComponent review={review}/>
                                                    <div className={'card-body'}>
                                                        <div className={'card-text'}>{review.comment}</div>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>)}
                                    </tbody>
                                </table>
                            </div>
                            : <div className={'d-flex justify-content-center align-items-center m-5'}>
                                <div className={'text-white fs-4'}>Отзывов пока нет, будьте первыми!</div>
                            </div>}
                    </div>
                    : <div className={'m-5'}>
                        <SpinnerComponent className={'text-white'}/>
                    </div>}
            </div>
            <ReviewFormComponent addReview={this.addReview}/>
        </div>;
    }
}

class GalleryComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {index: 0};
    }

    render() {
        return <div className={'d-flex justify-content-center'}>
            <div className={'gallery w-75'}>
                <Carousel activeIndex={this.state.index}
                          onSelect={(index, e) => this.setState({index: index})}>
                    <Carousel.Item>
                        <img src={'/img/gallery/gallery1.jpg'} alt='Первый слайд' className={'d-block w-100'}/>
                        <Carousel.Caption>
                            <h5>Хороший вид</h5>
                            <p>Ресторан располагается в самом красивом месте!</p>
                        </Carousel.Caption>
                    </Carousel.Item>
                    <Carousel.Item>
                        <img src={'/img/gallery/gallery2.jpg'} alt='Второй слайд' className={'d-block w-100'}/>
                        <Carousel.Caption>
                            <h5>Чистота и порядок</h5>
                            <p>Мы сделаем все, чтобы вы чувствовали себя как дома или даже лучше...</p>
                        </Carousel.Caption>
                    </Carousel.Item>
                    <Carousel.Item>
                        <img src={'/img/gallery/gallery3.jpg'} alt='Третий слайд' className={'d-block w-100'}/>
                        <Carousel.Caption>
                            <h5>Кухня Шефа</h5>
                            <p>Професионалы своего дела приготовят для вас свои деликатесы...</p>
                        </Carousel.Caption>
                    </Carousel.Item>
                    <Carousel.Item>
                        <img src={'/img/gallery/gallery4.jpg'} alt='Четвертый слайд' className={'d-block w-100'}/>
                        <Carousel.Caption>
                            <h5>Блюда на выбор</h5>
                            <p>Разнообразное меню для которого используются только продукты высшего качества!</p>
                        </Carousel.Caption>
                    </Carousel.Item>
                </Carousel>
            </div>
        </div>;
    }
}

function HomeComponent() {
    return <div>
        <div className={'info-container info-header d-flex justify-content-center align-items-center text-white'}>
            <div className={'info-about text-center'}>
                <div className={'my-5 fs-3'}>
                    Место, куда захочется вернуться!
                </div>
                <div className={'fs-5 lh-lg'}>
                    Благодаря современному дизайну, теплым тонам и видам на набережную, наш дизайн создан для того,
                    чтобы вы
                    чувствовали себя комфортно и по-домашнему.

                    В меню будут представлены продукты высочайшего качества, и наши гости получат качество и постоянство
                    блюд, которых нет нигде в нашем городе.
                </div>
            </div>
        </div>
        <div className={'info-container d-flex justify-content-around align-items-center text-white'}>
            <img src={'/img/gallery/dish.jpg'} alt={'Блюдо'} className={'info-dish px-5'}/>
            <div className={'info-menu text-end'}>
                <div className={'my-5 fs-3'}>
                    Каждый найдет что-нибудь свое...
                </div>
                <div className={'fs-5 lh-base'}>
                    Что же такого особенного у нас в меню?
                    Все блюда приговлены лучшими поварами проффесионалами! Только свежые продукты попадают к нам на
                    кухню,
                    здесь вы можете отведать ваши любимые блюда или попробовать что-нибудь новенькое.
                    Нет необходимости в чем-то себе отказывать, доступные цены не оставят никого равнодушным,
                    а если вы любите покушать дома с друзьями или в кругу семьи, наша доставка поможет вам с этим!
                </div>
                <a className={'btn btn-dark m-4 rounded-0 float-end'} href={'/menu'}>Скорее в меню</a>
            </div>
        </div>
        <div className={'info-container d-flex justify-content-center align-items-center text-white'}>
            <div className={'info-extra text-center'}>
                <div className={'fs-4 lh-base'}>
                    Нам очень важна ваша оценка, поэтому оставляйте свои отзывы и не забывайте забывайте писать на
                    почту либо звонить по номеру,
                    если возникнут какие-либо вопросы или пожелания по улучшению нашего сервиса.

                    Благодарим, что посетили наш ресторан, обязательно приходите еще!
                </div>
            </div>
        </div>
        <GalleryComponent/>
        <ReviewListComponent/>
    </div>;
}

ReactDOM.createRoot(document.getElementById('page-container')).render(<PageComponent><HomeComponent/></PageComponent>);