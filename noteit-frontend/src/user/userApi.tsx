import axios from "axios";
import {authConfig, withLogs} from "../shared";

const baseUrl = "http://localhost:8080/user";

interface UserProps {
    full_name: string;
    email: string;
    username: string;
}

export const getUser: (token: string) => Promise<UserProps> = (token) => {
    const url = baseUrl + "/details";
    return withLogs(axios.get(url, authConfig(token)), 'getUser');
}

export const updateFullName: (token: string, fullname: string) => Promise<UserProps> = (token, fullname) => {
    const url = baseUrl + "/update/fullname";
    return withLogs(axios.put(url, {updateString: fullname}, authConfig(token)), 'updateFullname');
}

export const updateUsername: (token: string, username: string) => Promise<UserProps> = (token, username) => {
    const url = baseUrl + "/update/username";
    return withLogs(axios.put(url, {updateString: username}, authConfig(token)), 'updateUsername');
}

export const updateEmail: (token: string, email: string) => Promise<UserProps> = (token, email) => {
    const url = baseUrl + "/update/email";
    return withLogs(axios.put(url, {updateString: email}, authConfig(token)), 'updateEmail');
}

export const updatePassword: (token: string, password: string) => Promise<UserProps> = (token, password) => {
    const url = baseUrl + "/update/password";
    return withLogs(axios.put(url, {updateString: password}, authConfig(token)), 'updatePassword');
}