import axios from "axios";
import {getLogger} from "../shared";

const baseUrl = 'localhost:8080';

const log = getLogger('authApi');

const config = {
    headers: {
        'Content-Type': 'application/json'
    }
};

const withLogs = (promise, fnName) => {
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

export const getMessage = () => {
    return withLogs(axios.get(`http://${baseUrl}/test/da`, config), "getMessage");
}