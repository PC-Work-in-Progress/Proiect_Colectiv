import {authConfig, withLogs} from "../shared";
import axios from "axios";

const homeUrl = "http://localhost:8080/files"

interface FileResponse {
    nume: string;
    content: string;
}

export const getFileContent: (token: string, fileId: string) => Promise<FileResponse> = async (token, fileId) => {
    const url = homeUrl + `/${fileId}`;
    return withLogs(axios.get(url, fileConfig(token)), 'getFileContent');
}

export const fileConfig = (token?: string) => ({
    headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
        responseType: "arraybuffer"
    }
});

