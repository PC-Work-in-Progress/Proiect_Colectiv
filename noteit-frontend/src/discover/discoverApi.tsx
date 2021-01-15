import {authConfig, withLogs} from "../shared";
import axios from "axios";
import {RoomProps} from "../home/room";
import {TagProps} from "./useDiscover";

const homeUrl = "http://localhost:8080"

export const getRooms: (token: string, searchText?: any) => Promise<RoomProps[]> = (token, searchText) => {
    if (!searchText) {
        searchText = "";
    }
    const url = homeUrl + `/rooms/${searchText}`;
    return withLogs(axios.get(url, authConfig(token)), 'getRooms');
}

export const getTags: (token: string) => Promise<TagProps[]> = (token) => {
    const url = homeUrl + "/tags/predefined";
    return withLogs(axios.get(url, authConfig(token)), 'getTags');
}

export const getRoomsByTags: (token: string, tags: string[]) => Promise<TagProps[]> = (token, tags) => {
    let url = homeUrl + "/rooms/filterRooms/";
    tags.forEach(t => {
        url += t + ",";
    })
    if (url[url.length - 1] === ",") {
        url.substring(0, url.length - 1);
    }
    return withLogs(axios.get(url, authConfig(token)), 'getRoomsByTags');
}