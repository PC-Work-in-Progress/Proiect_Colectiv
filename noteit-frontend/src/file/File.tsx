import React, {useContext, useEffect, useState} from "react";
import {IonContent, IonLoading} from '@ionic/react';
import {Header} from "../layout/Header";
import "./File.css"
import {RouteComponentProps} from "react-router";
import {getLogger} from "../shared";
import {AuthContext} from "../auth/AuthProvider";
import {getFileContent} from "./fileApi";

const log = getLogger("File");

interface FilePageProps extends RouteComponentProps<{
    id: string;
    fileId: string;
}> {
}

interface FileState {
    fileContent: String;
    fetchingFile: boolean;
    fetchFileError: Error | null;
}

const initialState: FileState = {
    fileContent: "",
    fetchFileError: null,
    fetchingFile: false
}

export const File: React.FC<FilePageProps> = ({history, match}) => {
    const {token} = useContext(AuthContext);
    const [fileState, setFileState] = useState(initialState);
    let {fileContent, fetchingFile, fetchFileError} = fileState;
    useEffect(fetchFileContentEffect, [token, match.params.id, match.params.fileId]);

    return (
        <IonContent>
            <div className="flex-page">
                <Header/>
                <pre>{fileContent}</pre>
                <IonLoading isOpen={fetchingFile} message="Fetching file"/>
                {fetchFileError &&
                <div className="create-room-error">{fetchFileError.message}</div>}
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
                log('fetchFileContent succeeded');
                if (!canceled) {
                    setFileState({fetchFileError: null, fetchingFile: false, fileContent: file});
                }
            } catch (error) {
                log('fetchFileContent failed');
                setFileState({...fileState, fetchFileError: error, fetchingFile: false})
            }
        }
    }
}

