import React from "react";
import ReactDOM from "react-dom/client";
import {PageComponent} from "./page.jsx";
import contacts from "../resources/contacts.json";
import "bootstrap/dist/css/bootstrap.min.css";

function ContactsComponent() {
    return <div className={'d-flex justify-content-center align-items-center h-100'}>
        <div className={'card w-50 shadow'}>
            <div className={'card-head rounded-top bg-light text-center fs-3 fw-semibold'}>
                <div className={'card-title'}>Контакты</div>
            </div>
            <div className={'card-body'}>
                <table className={'table table-striped'}>
                    <tbody>
                    <tr>
                        <td>
                            <div className={'fs-5 fw-semibold fst-italic'}>Адрес</div>
                        </td>
                        <td className={'text-end'}>
                            {contacts.addressList.map((address, i) =>
                                <div
                                    key={i}>{address.locality}, {address.country}, {address.street} {address.house}</div>)}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div className={'fs-5 fw-semibold fst-italic'}>Почта</div>
                        </td>
                        <td className={'text-end'}>
                            {contacts.emailList.map((email, i) =>
                                <div key={i}>{email}</div>)}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div className={'fs-5 fw-semibold fst-italic'}>Телефон</div>
                        </td>
                        <td className={'text-end'}>
                            {contacts.phoneList.map((phone, i) =>
                                <div key={i}>{phone}</div>)}
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div className={'fs-5 fw-semibold fst-italic'}>Время работы</div>
                        </td>
                        <td className={'text-end'}>
                            {contacts.workTimeList.map((workTime, i) =>
                                <div key={i}>{workTime.days}: {workTime.from} - {workTime.to}</div>)}
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>;
}

ReactDOM.createRoot(document.getElementById('page-container')).render(
    <PageComponent><ContactsComponent/></PageComponent>);
