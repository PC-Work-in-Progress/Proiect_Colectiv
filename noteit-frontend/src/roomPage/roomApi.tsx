import { authConfig, withLogs } from "../shared";
import { FileProps } from "./file/FileProps";
import axios from "axios";
import { ResponseMessage } from "./ResponeMessage";

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

export const getFilesConfig2 = (token?: string) => ({
    headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
        'Access-Control-Allow-Origin' : '*',
        'Access-Control-Allow-Methods' : "DELETE, POST, GET, OPTIONS",
        'Access-Control-Allow-Headers' : "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
    }
})

export const getApprovedFiles: (token: string, roomId: string) => Promise<FileProps[]> = (token, roomId) => {
    return withLogs(axios.get(`http://${baseUrl}/api/files/ApprovedFiles?roomId=${roomId}`, getFilesConfig(token)), 'getFiles');
}

export const getInReviewFiles: (token: string, roomId: string) => Promise<FileProps[]> = (token, roomId) => {
    return withLogs(axios.get(`http://${baseUrl}/api/files/InReviewFiles?roomId=${roomId}`, getFilesConfig(token)), 'getFiles');
}

export const acceptFile: (token: string, fileId: string) => Promise<ResponseMessage> = (token, fileId) => {
    return withLogs(axios.get(`http://${baseUrl}/api/files/AcceptFile/${fileId}`, getFilesConfig2(token)), 'acceptFile');
}

export const denyFile: (token: string, fileId: string) => Promise<ResponseMessage> = (token, fileId) => {
    return withLogs(axios.get(`http://${baseUrl}/api/files/DenyFile/${fileId}`, getFilesConfig2(token)), 'denyFile');
}

export const isAdmin: (token: string, roomId: string) => Promise<any> = (token, roomId) => {
    return withLogs(axios.get(`http://${baseUrl}/rooms/isAdmin/${roomId}`, getFilesConfig(token)), 'isAdmin');
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
    return withLogs(axios.post(`http://${baseUrl}/api/files/UploadFile?roomId=${roomId}&tags=tag1`,file, uploadConfig(token)), 'uploadFile');
}   
