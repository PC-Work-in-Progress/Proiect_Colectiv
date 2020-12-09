import {authConfig, withLogs} from "../shared";
import axios from "axios";

const homeUrl = "http://localhost:8080/files"

export const getFileContent: (token: string, fileId: string) => Promise<string> = (token, fileId) => {
    console.log(token);
    const url = homeUrl + `/${fileId}`;
    return withLogs(axios.get(url, authConfig(token)), 'getFileContent');
}

