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
    let formData = new FormData();
    formData.append("photo", values.current.file, values.current.file.name);

    try {
     uploadFile(formData, routeId)
    } catch (err) {
      console.log(err);
    }
  };


  return (
    <IonContent>
         <div className="flex-page">
                <Header/>
                <IonPopover
                    isOpen={showAddFile}
                    cssClass='create-room-popover'
                    backdropDismiss={false}>
                    
                    <UploadFile uploading={uploading} uploadFile={uploadFile} uploadFileError={uploadError}
                                hide={hideUploadFile}/>
                </IonPopover>
                <IonGrid class="grid-page ion-padding-bottom">
                  <IonRow class="fullscreen">
                            <IonCol class="fullscreen" size="8.5"></IonCol>
                            <div className="first-col">
                              <IonCard class="fullscreen">
                                  <IonCardHeader>
                                      <IonCardTitle>Room files</IonCardTitle>
                                  </IonCardHeader>
                                  <IonCardContent>
                                    {files && (<IonList>
                                        {files.map(({fileId, name, username, type, date, tags}) => <MyFile fileId = {fileId} name= {name} type={type} date={date} username={username} tags={tags}></MyFile>
                                        )}
                                    </IonList>)}
                                    <IonLoading isOpen={fetchingFiles} message="Fetching files"/>
                                    {fetchingFilesError &&
                                       <div className="create-room-error">{fetchingFilesError.message}</div>}
                                    </IonCardContent>
                              </IonCard>
                              </div>
                            <IonCol class="fullscreen" size="3.5">
                              
                              <div className="button-div">
                                
                                    {/*
                                    <IonButton color="secondary" onClick={() => showUploadFile()}>
                                        UploadFile</IonButton>
                                      */}
                                        <IonItem> <input type="file" onChange={(ev) => onFileChange(ev)}></input>
                                        </IonItem>
                                        <IonButton color="primary" expand="full" onClick={() => submitForm()}>
                                          Upload
                                        </IonButton>
                                </div>
                                <IonCard>
                                  <MyFile fileId = {file.fileId} name={file.name} type={file.type} date={file.date} username={file.username} tags={file.tags}></MyFile>
                                  <IonCardContent>
                                        <IonLoading isOpen={fetchingFile} message="Fetching file"/>
                                        {fetchingFileError &&
                                        <div className="create-room-error">{fetchingFileError.message}</div>}
                                    </IonCardContent>
                                </IonCard>
                            </IonCol>
                    </IonRow>
                </IonGrid>
         </div>
    </IonContent>
     )
}