import React from "react";
import ReactDOM from "react-dom/client";
import {PageComponent} from "./page.jsx";
import "bootstrap/dist/css/bootstrap.min.css";

function AboutComponent() {
    return <div className={'d-flex justify-content-center align-items-center h-100'}>
        <div className={'about w-75 p-5 shadow rounded-4 text-center text-white fs-5 lh-lg'}>
            Лучший ресторан в городе! Благодаря современному дизайну, теплым тонам и видам на набережную, наш дизайн
            создан для того, чтобы вы
            чувствовали себя комфортно и по-домашнему.

            В меню будут представлены продукты высочайшего качества, и наши гости получат качество и постоянство
            блюд, которых нет нигде в во всем свете.
        </div>
    </div>;
}

ReactDOM.createRoot(document.getElementById('page-container')).render(<PageComponent><AboutComponent/></PageComponent>);