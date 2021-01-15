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

export const reviewFileConfig = (token: string) => ({
    headers: {
        Authorization: `Bearer ${token}`,
    }
})

export const getApprovedFiles: (token: string, roomId: string) => Promise<FileProps[]> = (token, roomId) => {
    return withLogs(axios.get(`http://${baseUrl}/api/files/ApprovedFiles?roomId=${roomId}`, getFilesConfig(token)), 'getFiles');
}

export const getInReviewFiles: (token: string, roomId: string) => Promise<FileProps[]> = (token, roomId) => {
    console.log(token);
    return withLogs(axios.get(`http://${baseUrl}/api/files/InReviewFiles?roomId=${roomId}`, getFilesConfig(token)), 'getFiles');
}

export const acceptFile: (token: string, fileId: string, roomId: string) => Promise<ResponseMessage> = (token, fileId, roomId) => {
    console.log(token);
    return withLogs(axios.put(`http://${baseUrl}/api/files/AcceptFile/${fileId}?roomId=${roomId}`,{}, reviewFileConfig(token)), 'acceptFile');
}

export const denyFile: (token: string,roomId: string, fileId: string) => Promise<ResponseMessage> = (token,roomId, fileId) => {
    return withLogs(axios.put(`http://${baseUrl}/api/files/DenyFile/${fileId}?roomId=${roomId}`,{}, reviewFileConfig(token)), 'denyFile');
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

export const scanConfig = (token?: string) => ({
    headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${token}`,
        responseType: "arraybuffer"
    }
});

export interface UploadResponse {
    message: string;
} 


interface FileResponse {
    nume: string;
    content: string;
}

export const uploadFile: (token: string, file: FormData, roomId: string, tags: string) => Promise<FileProps> = (token, file, roomId, tags) => {
    console.log(token);
    return withLogs(axios.post(`http://${baseUrl}/api/files/UploadFile?roomId=${roomId}&tags=${tags}`,file, uploadConfig(token)), 'uploadFile');
}   


export const scanNotes: (token: string, file: FormData)  => Promise<FileResponse> = (token, file) => {
    return withLogs(axios.post(`http://${baseUrl}/api/files/recognition`,file, scanConfig(token)), 'scanNotes');

}