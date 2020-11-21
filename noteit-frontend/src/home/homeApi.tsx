import {UserProps} from "./user";
import {authConfig, withLogs} from "../shared";
import axios from "axios";
import {RoomProps} from "./room";
import {NotificationProps} from "./recentfiles";

const homeUrl = "localhost:8080"

export const getUser: (token: string) => Promise<UserProps> = (token) => {
    return withLogs(axios.get(homeUrl, authConfig(token)), 'getUser');
}

export const getRooms: (token: string) => Promise<RoomProps[]> = (token) => {
    return withLogs(axios.get(homeUrl, authConfig(token)), 'getRooms');
}

export const getRecentFiles: (token: string) => Promise<NotificationProps[]> = (token) => {
    return withLogs(axios.get(homeUrl, authConfig(token)), 'getRecentFiles');
}
