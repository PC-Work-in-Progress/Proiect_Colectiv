import { useCallback, useContext, useEffect, useReducer } from "react";
import { AuthContext } from "../auth/AuthProvider";
import { getLogger } from "../shared";
import { FileProps } from "./file";
import { getFile } from "./roomApi";
import {uploadFile as uploadFileApi} from "./roomApi"

const log = getLogger("useRoomPage");

export type UploadFileFn = (file: FormData, routeId: string) => void;
export type HideUploadFileFn = () => void;

interface RoomState {
    files: FileProps[];
    file: FileProps;
    fileId: string;
    showAddFile: boolean;
    uploading: boolean;
    uploadError?: Error | null;
    fetchingFilesError?: Error | null;
    fetchingFiles: boolean;
    fetchingFile: boolean; 
    fetchingFileError?: Error | null;
}

const initialState: RoomState = {
    files: [],
    fileId: "file1",
    file: {
        fileId: "",
        name: "",
        username: "",
        type: "",
        date: "",
        size: "",
        URL: ""
    },
    showAddFile: false,
    uploading: false,
    fetchingFiles: false,
    fetchingFile: false
}

interface ActionProps {
    type: string,
    payload?: any,
}

const SHOW_ADD_FILE = 'SHOW_ADD_FILE';
const UPLOAD_FILE_STARTED = 'UPLOAD_FILE_STARTED';
const UPLOAD_FILE_FAILED = 'UPLOAD_FILE_FAILED';
const UPLOAD_FILE_SUCCEEDED = 'UPLOAD_FILE_SUCCEEDED'
const HIDE_ADD_FILE = 'HIDE_ADD_FILE';
const FETCH_FILES_STARTED = 'FETCH_FILES_STARTED';
const FETCH_FILES_FAILED = 'FETCH_FILES_FAILED';
const FETCH_FILES_SUCCEEDED = 'FETCH_FILES_SUCCEEDED';
const FETCH_FILE_STARTED = 'FETCH_FILE_STARTED';
const FETCH_FILE_FAILED = 'FETCH_FILE_FAILED';
const FETCH_FILE_SUCCEEDED = 'FETCH_FILE_SUCCEEDED';

const reducer: (state: RoomState, action: ActionProps) => RoomState =
    (state, {type, payload}) => {
        switch (type) {
            case SHOW_ADD_FILE:
                return {...state, showAddFile: true}
            case HIDE_ADD_FILE:
                return {...state, showAddFile:false, uploadError: null}
            case UPLOAD_FILE_STARTED:
                return {...state, uploading: true, uploadError: null}
            case UPLOAD_FILE_FAILED:
                return {...state, uploading: false, uploadError:  payload.error};
            case UPLOAD_FILE_SUCCEEDED:
                let files = state.files;
                const file = payload.file
                const index = files.findIndex(f => f.fileId === file.fileId);
                if (index === -1) {
                    files.push(file);
                } else {
                    files[index] = file;
                }
                return {...state, uploading: false, files: files}
            case FETCH_FILES_STARTED:
                return {...state, fetchingFiles: true, fetchingFilesError: null}
            case FETCH_FILES_FAILED:
                return {... state, fetchingFiles: false, fetchingFileError: payload.error}
            case FETCH_FILES_SUCCEEDED:
                let files2 = state.files;
                payload.files.forEach((file: FileProps) => {
                const index = state.files.findIndex(f => f.fileId === file.fileId);
                if (index === -1) {
                    files2.push(file);
                } else {
                    files2[index] = file;
                }

                })
                return {...state, fetchingFiles: false, files: files2}

            case FETCH_FILE_STARTED:
                return {...state, fetchingFile: true,fetchingFileError: null }
            case FETCH_FILE_FAILED:
                return {...state, fetchingFile: false,fetchingFileError: payload.error}
            case FETCH_FILE_SUCCEEDED:
                return {...state, fetchingFile: false, file: payload.file}
            default:
                return state;

        }
    };

        export const useRoom = () => {
            const {token} = useContext(AuthContext);
            const [state, dispatch] = useReducer(reducer, initialState);
            const uploadFile = useCallback<UploadFileFn>(uploadFileCallback, [token]);
            const hideUploadFile = useCallback<HideUploadFileFn>(hideUploadFileCallback, []);
            const showUploadFile = useCallback<HideUploadFileFn>(showUploadFileCallback, []);

            useEffect(fetchFilesEffect, [token]);
            useEffect(fetchFileEffect, [state.fileId]);
            return {state, uploadFile, hideUploadFile, showUploadFile}

            async function hideUploadFileCallback() {
                log('hide UploadFile Popover');
                dispatch({type: HIDE_ADD_FILE
                })
            }

            async function showUploadFileCallback() {
                log('show UploadFile Popover')
                dispatch({type: SHOW_ADD_FILE})
            }

            async function uploadFileCallback(file: FormData, routeId: string) {
                try {
                    log('uploadFile started');
                    dispatch({type: UPLOAD_FILE_STARTED});
                    // File check and upload
                    const response = await uploadFileApi(token, file,routeId);
                    console.log(response)
                    dispatch({type:UPLOAD_FILE_SUCCEEDED, payload:{file: response}})
                }
                catch(error) {
                    dispatch({type: UPLOAD_FILE_FAILED, payload: {error}})
                }
            }

            function fetchFilesEffect() {
                let canceled = false;
                fetchFiles();
                return () => {
                    canceled = true;
                }

                async function fetchFiles() {
                    if (!token?.trim()) {
                        return;
                    }

                    try {
                        log('fetchFiles started')
                        dispatch({type: FETCH_FILES_STARTED})
                        // getFiles
                        let result: FileProps[] = [];
                        log('fetchFiles succeeded');
                        if(!canceled) {
                            dispatch({type: FETCH_FILES_SUCCEEDED, payload: {files: result}})
                        }
                    }
                    catch (error) {
                        log('fetchFiles failed')
                        dispatch({type: FETCH_FILES_SUCCEEDED, payload: {error}})
                    }
                }
            }

            function fetchFileEffect() {
                let canceled = false;
                fetchFile();
                return () => {
                    canceled = true;
                }

                async function fetchFile() {
                    if (!token?.trim()) {
                        return;
                    }
                    try {
                        log('fetchFile started');
                        dispatch({type: FETCH_FILE_STARTED});
                        //get file fileID ADD TO STARE
                        let result = await getFile(token, state.fileId)
                        //let result: FileProps = {fileId: "id", name:"SUBIECTE 2021", type:"pdf",userName:"Mano", tags:["tag"], date: new Date()};
                        log('fetchFile succeeded')
                        if(!canceled) {
                            dispatch({type: FETCH_FILE_SUCCEEDED, payload: {file: result}})
                        }
                    }
                    catch (error) {
                        log('fetchFile failed');
                        dispatch({type: FETCH_FILE_FAILED, payload:{error}})
                    }
                }

            }



            
        }
