import axios from "axios";

export const getLogger: (tag: string) => (...args: any) => void =
    tag => (...args) => console.log(tag, ...args);

export const baseUrl = 'localhost:8080';

const authUrl = `http://${baseUrl}/api/auth/login`;
const singupUrl = `http://${baseUrl}/api/auth/signup`

const log = getLogger('authApi');

export interface ResponseProps<T> {
    data: T;
}

export interface AuthProps {
    token: string;
  }

export function withLogs<T>(promise: Promise<ResponseProps<T>>, fnName: string): Promise<T> {
    log(`${fnName} - started`);
    return promise
        .then(res => {
            log(`${fnName} - succeeded`);
            return Promise.resolve(res.data);
        })
        .catch(err => {
            log(`${fnName} - failed`);
            return Promise.reject(err);
        });
}

export const config = {
    headers: {
        'Content-Type': 'application/json'
    }
};

export const getMessage: () => Promise<string> = () => {
    return withLogs(axios.get(`http://${baseUrl}/test/da`, config), "getMessage");
};

export const login: (username?: string, password?: string) => Promise<AuthProps> = (username, password) => {
    return withLogs(axios.post(authUrl, { username, password }, config), 'login');
  }

export const signup: (username?: string, password?: string, fullName?: string, email?: string) => Promise<AuthProps> = (username, password, fullName, email) => {
    return withLogs(axios.post(singupUrl, { username, password, fullName, email }, config), 'sign up');
  }