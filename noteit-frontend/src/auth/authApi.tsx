import axios from "axios";
import {config, withLogs} from "../shared";

export const baseUrl = 'localhost:8080';

const authUrl = `http://${baseUrl}/api/auth/login`;
const singupUrl = `http://${baseUrl}/api/auth/signup`

export interface AuthProps {
    token: string;
}

export const getMessage: () => Promise<string> = () => {
    return withLogs(axios.get(`http://${baseUrl}/test/da`, config), "getMessage");
};

export const login: (username?: string, password?: string) => Promise<AuthProps> = (username, password) => {
    return withLogs(axios.post(authUrl, {username, password}, config), 'login');
}

export const signup: (username?: string, password?: string, fullName?: string, email?: string) => Promise<AuthProps> = (username, password, fullName, email) => {
    return withLogs(axios.post(singupUrl, {username, password, fullName, email}, config), 'sign up');
}