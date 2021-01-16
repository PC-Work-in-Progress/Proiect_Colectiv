import React, {useEffect, useRef, useState} from "react";
import {RouteComponentProps} from "react-router-dom";
import {Header} from "../layout/Header";
import {MyFile} from "./file/File";
import {useRoom} from "./useRoomPage";
import {add, addCircle, checkmarkCircle, enter, search, searchCircle} from "ionicons/icons";
import {
    IonButton,
    IonCard,
    IonCardContent,
    IonCardHeader,
    IonCardTitle,
    IonCol,
    IonContent,
    IonGrid,
    IonInput,
    IonItem,
    IonList,
    IonLoading,
    IonModal,
    IonPopover,
    IonRow,
    IonIcon,
    IonChip,
    IonSelect,
    IonSelectOption
} from "@ionic/react";
import {ScanNotes} from "./ScanNotes";


interface RoomPageProps extends RouteComponentProps<{
    id: string;
}> {
}

export const RoomPage: React.FC<RoomPageProps> = ({history, match}) => {
    const {state, setState, uploadFile, hideUploadFile, showUploadFile, reviewFile} = useRoom(match.params.id);
    const {
        files, file, showAddFile, uploadError, uploading, fetchingFilesError, fetchingFiles,
        fetchingFile, fetchingFileError, isAdmin, acceptedFiles, predefined
    } = state;

    console.log('render');
    // let {tags} = state;
    const roomId = match.params.id
    const [tag, setTag] = useState('');
    const [addNewTag, setAddNewTag] = useState(false);
    const [allTags, setAllTags] = useState<string[]>([]);
    const [showScanNotes, setShowScanNotes] = useState(false)
    const [formData, setFormData] = useState<FormData>();

    const predefinedTags = [];
    for (var t of predefined) {
        predefinedTags.push(t.name);
    }
    const [predefinedTag, setPredefinedTag] = useState<string>('');

    interface InternalValues {
        file: any;
    }

    const values = useRef<InternalValues>({
        file: false,
    });

    const onFileChange = (fileChangeEvent: any) => {
        values.current.file = fileChangeEvent.target.files[0];
    };

    const hide = () => {
        setShowScanNotes(false);
    }

    const submitForm = async (uploadType: string) => {
        if (!values.current.file) {
            return false;
        }
        console.log(values.current.file)
        let formData = new FormData();
        formData.append("file", values.current.file, values.current.file.name);
        console.log(formData.get("file"));
        try {
            if (uploadType === "upload") {
                let stringTags = '';
                for (let i = 0; i < allTags.length; i++) {
                    stringTags += allTags[i];
                    if (i !== allTags.length - 1) {
                        stringTags += ',';
                    }
                }
                console.log(stringTags);
                uploadFile(formData, roomId, stringTags);
            } else {
                setFormData(formData);
                setShowScanNotes(true);

            }
        } catch (err) {
            console.log(err);
        }
    };

    return (
        <IonContent>
            <Header/>
            <IonPopover
                isOpen={showAddFile}
                cssClass='create-room-popover'
                backdropDismiss={false}>

            </IonPopover>
            <IonGrid>
                <IonRow>
                    <IonCol size="8.5">
                        <IonCard>
                            < IonCardHeader>
                                < IonCardTitle> In Review files </IonCardTitle>
                            </IonCardHeader>
                            < IonCardContent>
                                {files && isAdmin && (<IonList>
                                    {files.map(({fileId, name, type, username, date, URL, size, approved}) =>
                                        <MyFile
                                            key={name} fileId={fileId} name={name} type={type}
                                            date={date}
                                            username={username} URL={URL} size={size} approved={1}
                                            onView={() => {
                                                history.push(`/room/${roomId}/${fileId}`)
                                            }} onReview={reviewFile} isAdmin={isAdmin}></MyFile>
                                    )}</IonList>)
                                }
                                {files.length === 0 && <IonList>
                                    <MyFile key={"cheie"} fileId={"id"}
                                            name={"Momentan nu sunt fisiere in review"}
                                            type={"txt"} date={(new Date).toString()}
                                            username={"test"} URL={"url"}
                                            size={'0'} approved={1} onView={() => {
                                    }}> </MyFile>
                                </IonList>
                                }
                                <IonLoading isOpen={fetchingFiles} message="Fetching files"/>
                                {
                                    fetchingFilesError &&
                                    <div
                                        className="create-room-error">{fetchingFilesError.message}</div>
                                }
                            </IonCardContent>
                        </IonCard>
                        <IonCard>
                            <IonCardHeader>
                                <IonCardTitle>Room files</IonCardTitle>
                            </IonCardHeader>
                            <IonCardContent>
                                {acceptedFiles && (<IonList>
                                    {acceptedFiles.map(({fileId, name, type, username, date, URL, size, approved}) =>
                                        <MyFile key={name} fileId={fileId} name={name} type={type} date={date}
                                                username={username} URL={URL} size={size} approved={0} onView={() => {
                                            history.push(`/room/${roomId}/${fileId}`)
                                        }} onReview={reviewFile} isAdmin={isAdmin}></MyFile>
                                    )}</IonList>)}
                                {acceptedFiles.length === 0 && <IonList>
                                    <MyFile key={"cheie"} fileId={"id"} name={"Momentan nu sunt fisiere in room"}
                                            type={"txt"} date={(new Date).toString()} username={"test"} URL={"url"}
                                            size={'0'} approved={0} onView={() => {
                                    }}> </MyFile>
                                </IonList>
                                }
                                <IonLoading isOpen={fetchingFiles} message="Fetching files"/>
                                {fetchingFilesError &&
                                <div className="create-room-error">{fetchingFilesError.message}</div>}
                            </IonCardContent>
                        </IonCard>
                    </IonCol>
                    <IonCol size="3.5">

                        {/*
                    <IonButton color="secondary" onClick={() => showUploadFile()}>
                    UploadFile</IonButton>
                    */}

                        <IonItem>
                            <input type="file" onChange={(ev) => onFileChange(ev)}></input>
                        </IonItem>


                        <IonIcon icon={addCircle} onClick={e => setAddNewTag(true)}/>
                        <IonIcon icon={checkmarkCircle} onClick={e => {
                            let myTags = allTags;
                            if (!addNewTag) {
                                myTags.push(predefinedTag!);
                                setAllTags(myTags);
                            } else {
                                setAddNewTag(false);
                                if (tag !== '') {
                                    myTags.push(tag);
                                    setAllTags(myTags);
                                }
                                setTag('');
                            }
                        }}/>
                        {addNewTag && <IonInput placeholder="tags" value={tag} onIonInput={(e: any) => {
                            // tags = e.target.value;
                            setTag(e.target.value);
                        }}/>}
                        <IonSelect value={predefinedTag} onIonChange={e => {
                            setPredefinedTag(e.detail.value);
                        }}>
                            {predefinedTags && predefinedTags.map(elem => {
                                if(allTags.indexOf(elem) === -1) {
                                    return <IonSelectOption value={elem}>{elem}</IonSelectOption>
                                }
                            })}
                        </IonSelect>
                        <IonList>
                            {/* eslint-disable-next-line array-callback-return */}
                            {allTags && allTags.map(tag => {
                                if (tag !== '') {

                                    return <IonChip key={roomId + " " + tag} class="tag">{tag}</IonChip>
                                }
                            })}
                        </IonList>


                        <IonButton color="primary" expand="full" onClick={() => {
                            if (allTags.length > 0) {
                                submitForm("upload");
                            }
                        }}>
                            Upload
                        </IonButton>


                        <IonButton color="primary" expand="full" onClick={() => submitForm("scan")}>
                            Scan Notes
                        </IonButton>
                        <IonPopover
                            cssClass='create-room-popover'
                            isOpen={showScanNotes}
                            backdropDismiss={false}>
                            <ScanNotes uploadFile={uploadFile} file={formData} roomId={roomId} hide={hide}/>
                        </IonPopover>
                        {/*

                        <IonCard>

                        <MyFile fileId = {file.fileId} name= {file.name} type={file.type} date={file.date} username={file.username} URL={file.URL} size={file.size}></MyFile>

                        <IonCardContent>
                        <IonLoading isOpen={fetchingFile} message="Fetching file"/>
                        {fetchingFileError &&
                        <div className="create-room-error">{fetchingFileError.message}</div>}
                        </IonCardContent>
                        </IonCard>
                        */}

                    </IonCol>
                </IonRow>
            </IonGrid>
        </IonContent>
    )
}
