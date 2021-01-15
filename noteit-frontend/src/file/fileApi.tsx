import {withLogs} from "../shared";
import axios from "axios";

const homeUrl = "http://localhost:8080/api/files"

interface FileResponse {
    nume: string;
    content: string;
}

export const getFileContent: (token: string, fileId: string, roomId: string) => Promise<FileResponse> = async (token, fileId, roomId) => {
    const url = homeUrl + `/${fileId}?roomId=${roomId}`;
    return withLogs(axios.get(url, fileConfig(token)), 'getFileContent');
}

export const fileDownload: (token: string, fileId: string, roomId: string) => Promise<string> = async (token, fileId, roomId) => {
    const url = homeUrl + `/DownloadFile/${fileId}?roomId=${roomId}`;
    return withLogs(axios.put(url, {}, fileConfig(token)), 'downloadFile');
}

export const fileConfig = (token?: string) => ({
    headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
        responseType: "arraybuffer"
    }
});

