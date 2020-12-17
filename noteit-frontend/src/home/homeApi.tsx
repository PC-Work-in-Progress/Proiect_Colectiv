import {authConfig, withLogs} from "../shared";
import axios from "axios";
import {RoomProps} from "./room";
import {NotificationProps} from "./recentfiles";

const homeUrl = "http://localhost:8080"

interface Room {
    id: string;
    name: string;
}

interface UserProps2 {
    full_name: string;
    email: string;
    username: string;
}

export const getUser: (token: string) => Promise<UserProps2> = (token) => {
    const url = homeUrl + "/user/details";
    return withLogs(axios.get(url, authConfig(token)), 'getUser');
}

export const getRooms: (token: string) => Promise<RoomProps[]> = (token) => {
    const url = homeUrl + "/rooms/roomsUser";
    return withLogs(axios.get(url, authConfig(token)), 'getRooms');
}

export const getRecentFiles: (token: string, page?: number) => Promise<NotificationProps[]> = (token, page) => {
    let url = homeUrl + "/api/files/recentFiles";
    if (!page){
        page = 0;
    }
    url = url + `/${page}`;
    return withLogs(axios.get(url, authConfig(token)), 'getRecentFiles');
}

export const addRoom: (token: string, room: Room) => Promise<RoomProps[]> = (token, room) => {
    const url = homeUrl + `/rooms/createRoom`;
    return withLogs(axios.post(url, room, authConfig(token)), 'postRoom');
}
