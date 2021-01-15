import axios from "axios";
import {authConfig, withLogs} from "../shared";

const baseUrl = "http://localhost:8080/api/notifications";


interface NotificationProps {
    id: string;
    message: string;
    viewed: number;
}

export const GetNotifications: (token: string) => Promise<NotificationProps[]> = (token) => {
    const url = baseUrl + "/GetNotifications";
    return withLogs(axios.get(url, authConfig(token)), 'getNotifications');
}