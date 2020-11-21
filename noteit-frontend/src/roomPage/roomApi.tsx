import { authConfig, withLogs } from "../shared";
import { FileProps } from "./file/FileProps";
import axios from "axios";

const homeUrl = "localhost:8080"

export const baseUrl = 'localhost:8080'

export const getFile: (token: string, idFile: string) => Promise<FileProps> = (token, idFile) => {
    return withLogs(axios.get(`http://${baseUrl}/files/details/${idFile}`, authConfig(token)), 'getFile');
}

export const getFilesConfig = (token?: string, roomId?: string) => ({
    headers: {
        'Content-Type': 'application/json',
        'RoomId' : `${roomId}`,
        Authorization: `Bearer ${token}`,
    }
})

export const getFiles: (token: string, roomId: string) => Promise<FileProps[]> = (token, roomId) => {
    return withLogs(axios.get(`http://${baseUrl}/files`, getFilesConfig(token, roomId)), 'getFiles');
}

export const uploadConfig = (token?: string, roomId?: string) => ({
    headers: {
        'Content-Type': 'multipart/form-data',
        RoomId : "1",
        Authorization: `Bearer ${token}`,
    },
});

export interface UploadResponse {
    message: string;
} 

export const uploadFile: (token: string, file: FormData, roomId: string) => Promise<FileProps> = (token, file, roomId) => {
    console.log(file.get("file"));
    console.log(token);
    return withLogs(axios.post(`http://${baseUrl}/upload`,file, uploadConfig(token, roomId)), 'uploadFile');
}   
