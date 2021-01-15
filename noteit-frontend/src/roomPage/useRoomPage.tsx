import { useCallback, useContext, useEffect, useReducer, useState } from "react";
import { AuthContext } from "../auth/AuthProvider";
import { getLogger } from "../shared";
import { FileProps } from "./file";
import { getFile, getApprovedFiles, getInReviewFiles, acceptFile, denyFile, isAdmin } from "./roomApi";
import {uploadFile as uploadFileApi} from "./roomApi"



const log = getLogger("useRoomPage");

export type UploadFileFn = (file: FormData, routeId: string, tags: string) => void;
export type ScanNotesFn = (file: FormData) => string;
export type HideUploadFileFn = () => void;
export type ReviewFileFn = (fileId: string, type: string) => void;

interface RoomState {
    files: FileProps[];
    acceptedFiles: FileProps[];
    file: FileProps;
    fileId: string;
    showAddFile: boolean;
    uploading: boolean;
    uploadError?: Error | null;
    fetchingFilesError?: Error | null;
    fetchingFiles: boolean;
    fetchingFile: boolean; 
    fetchingFileError?: Error | null;
    isAdmin: boolean;
    tags: string,
}

const initialState: RoomState = {
    files: [],
    acceptedFiles: [],
    fileId: "file1",
    file: {
        fileId: "",
        name: "",
        username: "",
        type: "",
        date: "",
        size: "",
        URL: "",
        approved: 0
    },
    showAddFile: false,
    uploading: false,
    fetchingFiles: false,
    fetchingFile: false,
    isAdmin: false,
    tags: "",
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
const REVIEW_FILE_STARTED = 'REVIEW_FILE_STARTED';
const REVIEW_FILE_FAILED = 'REVIEW_FILE_FAILED';
const REVIEW_FILE_SUCCEEDED = 'REVIEW_FILE_SUCCEEDED';
const FETCH_ISADMIN_STARTED = 'FETCH_ISADMIN_STARTED';
const FETCH_ISADMIN_FAILED = 'FETCH_ISADMIN_FAILED';
const FETCH_ISADMIN_SUCCEDED ='FETCH_ISADMIN_SUCCEDED';


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
                const file = payload.file;
                const index = files.findIndex(f => f.fileId === file.fileId);
                if (index === -1) {
                    files.push(file);
                } else {
                    //files.push(file);
                    files[index] = file;
                }
                return {...state, uploading: false, files: files}
            case FETCH_FILES_STARTED:
                return {...state, fetchingFiles: true, fetchingFilesError: null}
            case FETCH_FILES_FAILED:
                return {... state, fetchingFiles: false, fetchingFileError: payload.error}
            case FETCH_FILES_SUCCEEDED:
                let files2 = payload.files;
                /*
                payload.files.forEach((file: FileProps) => {
                const index = state.files.findIndex(f => f.fileId === file.fileId);
                if (index === -1) {
                    files2.push(file);
                } else {
                    files2[index] = file;
                    //files2.push(file);
                }
                //console.log(files2);
                })
                */
                let acceptedFiles1 = payload.acceptedFiles;
                /*
                payload.acceptedFiles.forEach((afile: FileProps) => {
                    const index = state.acceptedFiles.findIndex(f => f.fileId === afile.fileId);
                    if (index === -1) {
                        acceptedFiles1.push(afile);
                    } else {
                        acceptedFiles1[index] = afile;
                        //files2.push(file);
                    }
                    //console.log(files2);
                    })
                */
                return {...state, fetchingFiles: false, files: files2, acceptedFiles: acceptedFiles1}

            case FETCH_FILE_STARTED:
                return {...state, fetchingFile: true,fetchingFileError: null }
            case FETCH_FILE_FAILED:
                return {...state, fetchingFile: false,fetchingFileError: payload.error}
            case FETCH_FILE_SUCCEEDED:
                return {...state, fetchingFile: false, file: payload.file}


            case REVIEW_FILE_STARTED:
                return state;
            case REVIEW_FILE_FAILED:
                return state;
            case REVIEW_FILE_SUCCEEDED:
                let files3 = state.files;
                let acceptedFiles3 = state.acceptedFiles;
                var i, poz = 0;
                for(i = 0; i < files3.length; i = i + 1)
                    if ( files3[i].fileId === payload.fileId )
                        poz = i;
                if(payload.type == "accept")
                    acceptedFiles3.push(files3[poz])
                files3.splice(poz,1)
                
                return {...state, files: files3, acceptedFiles: acceptedFiles3}


            case FETCH_ISADMIN_STARTED:
                return state;
            case FETCH_ISADMIN_FAILED:
                return {...state, isAdmin: false}
            case FETCH_ISADMIN_SUCCEDED:
                return {...state, isAdmin: true}

            default:
                return state;
        }
    };

        export const useRoom = (roomId: string) => {
            const [ignored, setState] = useState<RoomState>(initialState);
            const {token} = useContext(AuthContext);
            const [state, dispatch] = useReducer(reducer, initialState);
            const uploadFile = useCallback<UploadFileFn>(uploadFileCallback, [token]);
            
            const hideUploadFile = useCallback<HideUploadFileFn>(hideUploadFileCallback, []);
            const showUploadFile = useCallback<HideUploadFileFn>(showUploadFileCallback, []);

            const reviewFile = useCallback<ReviewFileFn>(reviewFileCallback, [token]);

            useEffect(fetchFilesEffect, [token]);
            useEffect(fetchFileEffect, [state.fileId,token]);
            useEffect(fetchIsAdminEffect, [token])

            return {state,setState, uploadFile, hideUploadFile, showUploadFile, reviewFile}

            async function hideUploadFileCallback() {
                log('hide UploadFile Popover');
                dispatch({type: HIDE_ADD_FILE})
            }

            async function showUploadFileCallback() {
                log('show UploadFile Popover')
                dispatch({type: SHOW_ADD_FILE})
            }

            async function reviewFileCallback(fileId: string, type: string) 
            {
                try {
                    log('acceptFile started');
                    dispatch({type: REVIEW_FILE_STARTED});
                    console.log(token);
                    if (type === "accept") {
                        const response = await acceptFile(token, fileId, roomId);
                    }
                    else {
                        const response = await denyFile(token,roomId, fileId);
                    }
                    dispatch({type: REVIEW_FILE_SUCCEEDED, payload: {fileId: fileId, type: type}});
                }
                catch(error) {
                    dispatch({type: REVIEW_FILE_FAILED, payload: {error}})
                }
            }

            async function uploadFileCallback(file: FormData, routeId: string, tags: string) {
                try {
                    log('uploadFile started');
                    dispatch({type: UPLOAD_FILE_STARTED});
                    // File check and upload
                    //console.log(routeId)
                    const response = await uploadFileApi(token, file,routeId, tags);
                    //console.log(response)
                    dispatch({type:UPLOAD_FILE_SUCCEEDED, payload:{file: response}})
                }
                catch(error) {
                    dispatch({type: UPLOAD_FILE_FAILED, payload: {error}})
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

            

            function fetchIsAdminEffect() {
                let canceled = false;
                fetchIsAdmin();
                return () => {
                    canceled = true;
                }

                async function fetchIsAdmin() {
                    if (!token?.trim()) {
                        return;
                    }

                    try {
                        log('fetchIsAdmin started')
                        dispatch({type: FETCH_ISADMIN_STARTED})
                        let result = await isAdmin(token, roomId)
                        if(!canceled) {
                            dispatch({type: FETCH_ISADMIN_SUCCEDED, payload: {admin: result}})
                        }
                    }
                    catch (error) {
                        dispatch({type: FETCH_ISADMIN_FAILED, payload: {error}})
                    }
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
                        let result = await getInReviewFiles(token, roomId);
                        let result2 = await getApprovedFiles(token, roomId);
                        //let result: FileProps[] = [];
                        log('fetchFiles succeeded');
                        if(!canceled) {
                            dispatch({type: FETCH_FILES_SUCCEEDED, payload: {files: result, acceptedFiles: result2}})
                        }
                    }
                    catch (error) {
                        log('fetchFiles failed')
                        dispatch({type: FETCH_FILES_FAILED, payload: {error}})
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
