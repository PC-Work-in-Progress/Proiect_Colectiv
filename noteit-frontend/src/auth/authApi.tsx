import axios from "axios";

export const getLogger: (tag: string) => (...args: any) => void =
    tag => (...args) => console.log(tag, ...args);

export const baseUrl = 'localhost:8080';

const log = getLogger('authApi');

export interface ResponseProps<T> {
    data: T;
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