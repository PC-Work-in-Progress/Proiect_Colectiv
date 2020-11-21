import { IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonCol, IonContent, IonGrid, IonItem, IonList, IonLoading, IonPopover, IonRow } from "@ionic/react";
import React from "react";
import { RouteComponentProps } from "react-router-dom";
import { Header } from "../layout/Header";
import { MyFile } from "./file/File";
import { UploadFile } from "./UploadFile";
import { useRoom } from "./useRoomPage";

interface RoomPageProps extends RouteComponentProps<{
  id?: string;
}> {}

export const RoomPage: React.FC<RouteComponentProps> = ({history}) => {
  const {state, uploadFile, hideUploadFile, showUploadFile} = useRoom();
  const {
    files, file, showAddFile, uploadError, uploading, fetchingFilesError, fetchingFiles,
    fetchingFile, fetchingFileError
  } = state
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
                                        {files.map(({fileId, name, userName, type, date, tags}) => <MyFile fileId = {fileId} name= {name} type={type} date={date} userName={userName} tags={tags}></MyFile>
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
                                    <IonButton color="secondary" onClick={() => showUploadFile()}>
                                        UploadFile</IonButton>
                                </div>
                                <IonCard class="file">
                                  <MyFile fileId = {file.fileId} name={file.name} type={file.type} date={file.date} userName={file.userName} tags={file.tags}></MyFile>
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