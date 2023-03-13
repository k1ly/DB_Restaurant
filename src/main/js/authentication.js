import React from "react";
import axios from "axios";
import Cookies from "js-cookie";
import "bootstrap/dist/css/bootstrap.min.css";

export function authenticate(setUser) {
    let loadUser = () =>
        axios.post('/login/user', null, {
            headers: {
                'Authorization': sessionStorage.getItem('token') ? `Bearer ${sessionStorage.getItem('token')}` : null,
                'Accept': 'application/json'
            }
        }).then(result => {
            if (result.data.role.name.match('^GUEST|MANAGER|ADMIN$'))
                Cookies.remove('cart');
            setUser(result.data);
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
            if (error.response.status === 401) {
                sessionStorage.removeItem('token');
                location.href = '/error/401';
            }
        });
    if (Cookies.get('remember-me') && !sessionStorage.getItem('token')) {
        axios.post('/login', null, {
            headers: {
                'Authorization': null,
                'Accept': 'text/plain',
                'Content-Type': 'application/json'
            }
        }).then(result => {
            sessionStorage.setItem('token', result.data);
            loadUser();
        }).catch(error => {
            console.error('Error:', error.response ? error.response.data : error);
        })
    } else loadUser();
}

export function forget(callback) {
    axios.post('/logout', null, {
        headers: {
            'Authorization': null,
            'Accept': 'text/plain'
        }
    }).then(result => {
        sessionStorage.removeItem('token');
        Cookies.remove('remember-me');
        callback();
    }).catch(error => {
        console.error('Error:', error.response ? error.response.data : error);
    })
}

export function clearCookie(name) {
    Cookies.remove(name);
}