import { IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonCol, IonContent, IonGrid, IonItem, IonList, IonLoading, IonPopover, IonRow } from "@ionic/react";
import React, { useRef } from "react";
import { RouteComponentProps } from "react-router-dom";
import { Header } from "../layout/Header";
import { MyFile } from "./file/File";
import UFile from "./UFile";
import { UploadFile } from "./UploadFile";
import { useRoom } from "./useRoomPage";


interface RoomPageProps extends RouteComponentProps<{
  roomId: string;
}> {}

export const RoomPage: React.FC<RoomPageProps> = ({history, match}) => {
  const {state, uploadFile, hideUploadFile, showUploadFile} = useRoom();
  const {
    files, file, showAddFile, uploadError, uploading, fetchingFilesError, fetchingFiles,
    fetchingFile, fetchingFileError
  } = state

  const routeId = match.params.roomId

  interface InternalValues {
    file: any;
  }

  const values = useRef<InternalValues>({
    file: false,
  });

  const onFileChange = (fileChangeEvent: any) => {
    values.current.file = fileChangeEvent.target.files[0];
    
  };

  const submitForm = async () => {
    if (!values.current.file) {
      return false;
    }
    console.log(values.current.file)
    let formData = new FormData();
    formData.append("file", values.current.file, values.current.file.name);
    console.log(formData.get("file"));
    try {
     uploadFile(formData, routeId)
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
                    
                    <UploadFile uploading={uploading} uploadFile={uploadFile} uploadFileError={uploadError}
                                hide={hideUploadFile}/>
                </IonPopover>
                <IonGrid>
                  <IonRow >
                            <IonCol size="8.5"></IonCol>
                              <IonCard>
                                  <IonCardHeader>
                                      <IonCardTitle>Room files</IonCardTitle>
                                  </IonCardHeader>
                                  <IonCardContent>
                                    {files && (<IonList>
                                        {files.map(({fileId,name,type,username,date,URL,size}) => <MyFile key={name} fileId = {fileId} name= {name} type={type} date={date} username={username} URL={URL} size={size}></MyFile>
                                        )}
                                    </IonList>)}
                                    <IonLoading isOpen={fetchingFiles} message="Fetching files"/>
                                    {fetchingFilesError &&
                                       <div className="create-room-error">{fetchingFilesError.message}</div>}
                                    </IonCardContent>
                              </IonCard>
                            <IonCol size="3.5">
                                
                                    {/*
                                    <IonButton color="secondary" onClick={() => showUploadFile()}>
                                        UploadFile</IonButton>
                                      */}
                                        <IonItem> <input type="file" onChange={(ev) => onFileChange(ev)}></input>
                                        </IonItem>
                                        <IonButton color="primary" expand="full" onClick={() => submitForm()}>
                                          Upload
                                        </IonButton>
                                <IonCard>
                                   {/*
                                    
                                      
                                  <MyFile fileId = {file.fileId} name= {file.name} type={file.type} date={file.date} username={file.username} URL={file.URL} size={file.size}></MyFile>
                                  */}
                                  <IonCardContent>
                                        <IonLoading isOpen={fetchingFile} message="Fetching file"/>
                                        {fetchingFileError &&
                                        <div className="create-room-error">{fetchingFileError.message}</div>}
                                    </IonCardContent>
                                </IonCard>
                            </IonCol>
                    </IonRow>
                </IonGrid>
    </IonContent>
     )
}