import { authConfig, withLogs } from "../shared";
import { FileProps } from "./file/FileProps";
import axios from "axios";

export const baseUrl = 'localhost:8080'

export const getFile: (token: string, idFile: string) => Promise<FileProps> = (token, idFile) => {
    return withLogs(axios.get(`http://${baseUrl}/files/details/${idFile}`, authConfig(token)), 'getFile');
}

export const getFilesConfig = (token?: string) => ({
    headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
    }
})

export const getFiles: (token: string, roomId: string) => Promise<FileProps[]> = (token, roomId) => {
    return withLogs(axios.get(`http://${baseUrl}/files/${roomId}`, getFilesConfig(token)), 'getFiles');
}

export const uploadConfig = (token?: string) => ({
    headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${token}`,
    },
});

export interface UploadResponse {
    message: string;
} 

export const uploadFile: (token: string, file: FormData, roomId: string) => Promise<FileProps> = (token, file, roomId) => {
    return withLogs(axios.post(`http://${baseUrl}/upload/${roomId}`,file, uploadConfig(token)), 'uploadFile');
}   
