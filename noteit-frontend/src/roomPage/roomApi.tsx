import { authConfig, withLogs } from "../shared";
import { FileProps } from "./file/FileProps";
import axios from "axios";

const homeUrl = "localhost:8080"

export const getFile: (token: string) => Promise<FileProps> = (token) => {
    return withLogs(axios.get(homeUrl, authConfig(token)), 'getFile');
}

export const getFiles: (token: string) => Promise<FileProps[]> = (token) => {
    return withLogs(axios.get(homeUrl, authConfig(token)), 'getFiles');
}
