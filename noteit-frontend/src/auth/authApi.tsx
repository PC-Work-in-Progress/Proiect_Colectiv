import axios from "axios";
import {config, withLogs} from "../shared";

export const baseUrl = 'localhost:8080';

const authUrl = `http://${baseUrl}/api/auth/signin`;
const singupUrl = `http://${baseUrl}/api/auth/signup`;

export interface AuthProps {
    accessToken: string;
    tokenType: string;
}

export interface ResponseProps {
    message: string;
    success: boolean;
}

export const getMessage: () => Promise<string> = () => {
    return withLogs(axios.get(`http://${baseUrl}/test/da`, config), "getMessage");
};

export const login: (username?: string, password?: string) => Promise<AuthProps> = (username, password) => {
    return withLogs(axios.post(authUrl, {username, password}, config), 'login');
}

export const signup: (username?: string, password?: string, full_name?: string, email?: string) => Promise<ResponseProps> = (username, password, full_name, email) => {
    return withLogs(axios.post(singupUrl, {username, password, full_name, email}, config), 'sign up');
}