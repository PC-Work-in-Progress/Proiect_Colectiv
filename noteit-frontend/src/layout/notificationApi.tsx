import axios from "axios";
import {authConfig, withLogs} from "../shared";

const baseUrl = "http://localhost:8080/api/notifications";


export interface NotificationMessageProps {
    id: string;
    message: string;
    viewed: number;
}

export const GetNotifications: (token: string) => Promise<NotificationMessageProps[]> = (token) => {
    const url = baseUrl + "/GetNotifications";
    return withLogs(axios.get(url, authConfig(token)), 'getNotifications');
}

export const ReadNotification: (token: string, id: string) => Promise<String> = (token, id) => {
    const url = baseUrl + "/ReadNotification/" + id ;
    return withLogs(axios.put(url,{}, authConfig(token)), 'readNotification');
}