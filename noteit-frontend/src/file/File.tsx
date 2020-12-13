import React, {useContext, useEffect, useState} from "react";
import {IonButton, IonCard, IonCardContent, IonContent, IonLoading, IonTabButton} from '@ionic/react';
import {Header} from "../layout/Header";
import "./File.css"
import {RouteComponentProps} from "react-router";
import {getLogger} from "../shared";
import {AuthContext} from "../auth/AuthProvider";
import {fileDownload, getFileContent} from "./fileApi";

const log = getLogger("File");

interface FilePageProps extends RouteComponentProps<{
    id: string;
    fileId: string;
}> {
}

interface FileState {
    fileContent: String;
    fileName: String;
    fetchingFile: boolean;
    fetchFileError: Error | null;
    objectUrl?: any;
    imageUrl?: any;
}

const initialState: FileState = {
    fileContent: "",
    fetchFileError: null,
    fetchingFile: false,
    fileName: ""
}

export const File: React.FC<FilePageProps> = ({history, match}) => {
    const {token} = useContext(AuthContext);
    const [fileState, setFileState] = useState(initialState);
    let {fileContent, fetchingFile, fetchFileError, objectUrl, imageUrl} = fileState;
    useEffect(fetchFileContentEffect, [token, match.params.id, match.params.fileId]);

    return (
        <IonContent>
            <div className="flex-page">
                <Header/>
                {objectUrl && <iframe title="ceva" className="pdf" src={objectUrl}/>}
                {imageUrl && <img className="file-image" src={imageUrl}/>}
                {fileContent !== "" && <IonCard class="file-content-container">
                    <IonCardContent class="file-card-content ion-align-self-center">
                        <pre className="file-content">{fileContent}</pre>
                    </IonCardContent>
                </IonCard>}
                {!objectUrl && <IonButton color="secondary" class="download-btn" onClick={() => {
                    downloadFile();
                }
                }>Download</IonButton>}
                <IonLoading isOpen={fetchingFile} message="Fetching file"/>
                {fetchFileError &&
                <div className="error">{fetchFileError.message}</div>}
            </div>
        </IonContent>
    );

    function fetchFileContentEffect() {
        let canceled = false;
        fetchFileContent();
        return () => {
            canceled = true;
        }

        async function fetchFileContent() {
            try {
                log(`fetchFileContent started`);
                setFileState({...fileState, fetchingFile: true, fetchFileError: null});
                // server get file data
                let file = await getFileContent(token, match.params.fileId);
                let arrayBuffer = base64ToArrayBuffer(file.content);
                let objectURL;
                let imageURL;
                let content;
                const ext = file.nume.split(".")[1];
                if (ext === "pdf") {
                    objectURL = "data:application/pdf;base64, " + file.content;
                } else {
                    if (["jpg", "jpeg", "png"].indexOf(ext) >= 0) {
                        imageURL = "data:image/jpg;base64, " + file.content;
                    } else {
                        content = ab2str(arrayBuffer);
                    }
                }
                log('fetchFileContent succeeded');
                if (!canceled) {
                    setFileState({
                        fetchFileError: null,
                        fetchingFile: false,
                        fileContent: content || "",
                        fileName: file.nume,
                        objectUrl: objectURL,
                        imageUrl: imageURL
                    });
                }
            } catch (error) {
                log('fetchFileContent failed');
                if (error.response.status === 417) {
                    error = {message: "File not found"};
                }
                setFileState({...fileState, fetchFileError: error, fetchingFile: false})
            }
        }
    }

    function base64ToArrayBuffer(base64: string) {
        const binary_string = window.atob(base64);
        const len = binary_string.length;
        const bytes = new Uint8Array(len);
        for (let i = 0; i < len; i++) {
            bytes[i] = binary_string.charCodeAt(i);
        }
        return bytes.buffer;
    }

    function ab2str(buf: ArrayBuffer) {
        return new TextDecoder().decode(buf);
    }

    function downloadFile() {
        let canceled = false;
        download();
        return () => {
            canceled = true;
        }

        async function download() {
            try {
                log(`download started`);
                // setFileState({...fileState, fetchingFile: true, fetchFileError: null});
                // server get file data
                // let file = await fileDownload(token, match.params.fileId);
                // console.log(file);
            } catch (error) {
                log('download failed');
                if (error.response.status === 417) {
                    error = {message: "File not found"};
                }
                setFileState({...fileState, fetchFileError: error, fetchingFile: false})
            }
        }
    }
}

