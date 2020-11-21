import {UserProps} from "./user";
import {authConfig, withLogs} from "../shared";
import axios from "axios";
import {RoomProps} from "./room";
import {NotificationProps} from "./recentfiles";

const homeUrl = "localhost:8080"

interface Room {
    name: string;
}

export const getUser: (token: string) => Promise<UserProps> = (token) => {
    const url = homeUrl + "/user/details";
    return withLogs(axios.get(url, authConfig(token)), 'getUser');
}

export const getRooms: (token: string) => Promise<RoomProps[]> = (token) => {
    const url = homeUrl + "/rooms/roomsUser";
    return withLogs(axios.get(url, authConfig(token)), 'getRooms');
}

export const getRecentFiles: (token: string) => Promise<NotificationProps[]> = (token) => {
    return withLogs(axios.get(homeUrl, authConfig(token)), 'getRecentFiles');
}

export const addRoom: (token: string, room: Room) => Promise<RoomProps[]> = (token, room) => {
    const url = homeUrl + `/rooms/createRoom/${room.name}`;
    return withLogs(axios.post(url, authConfig(token)), 'postRoom');
}
