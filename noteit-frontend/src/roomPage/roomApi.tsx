import { authConfig, withLogs } from "../shared";
import { FileProps } from "./file/FileProps";
import axios from "axios";

const homeUrl = "localhost:8080"

export const baseUrl = 'localhost:8080'

export const getFile: (token: string, idFile: string) => Promise<FileProps> = (token, idFile) => {
    return withLogs(axios.get(`http://${baseUrl}/files/details/${idFile}`, authConfig(token)), 'getFile');
}

export const getFiles: (token: string) => Promise<FileProps[]> = (token) => {
    return withLogs(axios.get(homeUrl, authConfig(token)), 'getFiles');
}
