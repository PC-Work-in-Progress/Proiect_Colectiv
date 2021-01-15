import { IonButton, IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonCol, IonContent, IonGrid, IonInput, IonItem, IonList, IonLoading, IonPopover, IonRow } from "@ionic/react";
import React, { useRef } from "react";
import { RouteComponentProps } from "react-router-dom";
import { Header } from "../layout/Header";
import { MyFile } from "./file/File";
import { useRoom } from "./useRoomPage";


interface RoomPageProps extends RouteComponentProps<{
  id: string;
}> {}

export const RoomPage: React.FC<RoomPageProps> = ({history, match}) => {
  const {state,setState, uploadFile,scanNotes, hideUploadFile, showUploadFile, reviewFile} = useRoom(match.params.id);
  const {
    files, file, showAddFile, uploadError, uploading, fetchingFilesError, fetchingFiles,
    fetchingFile, fetchingFileError, isAdmin, acceptedFiles
  } = state;

  let { tags } = state;
  const roomId = match.params.id
  interface InternalValues {
    file: any;
  }

  const values = useRef<InternalValues>({
    file: false,
  });

  const onFileChange = (fileChangeEvent: any) => {
    values.current.file = fileChangeEvent.target.files[0];
    
  };

  const submitForm = async (uploadType: string) => {
    if (!values.current.file) {
      return false;
    }
    console.log(values.current.file)
    let formData = new FormData();
    formData.append("file", values.current.file, values.current.file.name);
    console.log(formData.get("file"));
    try {
      if(uploadType === "upload" ) {
          uploadFile(formData, roomId, tags)
      }
      else {
          scanNotes(formData)
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
                  <IonRow >
                            <IonCol size="8.5">
                              <IonCard>
                                  <IonCardHeader>
                                      <IonCardTitle>In Review files</IonCardTitle>
                                  </IonCardHeader>
                                  <IonCardContent>
                                    {files && isAdmin && (<IonList>
                                        {files.map(({fileId,name,type,username,date,URL,size, approved}) => <MyFile key={name} fileId = {fileId} name= {name} type={type} date={date} username={username} URL={URL} size={size} approved = {1} onView={() => {history.push(`/room/${roomId}/${fileId}`)}} onReview={reviewFile} isAdmin = {isAdmin}></MyFile>
                                        )}</IonList>)}
                                    {files.length === 0 &&  <IonList>
                                          <MyFile key = {"cheie"} fileId = {"id"} name = {"Momentan nu sunt fisiere in review"} type={"txt"} date={(new Date).toString()} username={"test"} URL={"url"} size={'0'} approved={1} onView={() => {}}> </MyFile>
                                    </IonList>
                                    }
                                    <IonLoading isOpen={fetchingFiles} message="Fetching files"/>
                                    {fetchingFilesError &&
                                       <div className="create-room-error">{fetchingFilesError.message}</div>}
                                    </IonCardContent>
                              </IonCard>
                              <IonCard>
                                <IonCardHeader>
                                      <IonCardTitle>Room files</IonCardTitle>
                                </IonCardHeader>
                                <IonCardContent>
                                  {acceptedFiles && (<IonList>
                                        {acceptedFiles.map(({fileId,name,type,username,date,URL,size, approved}) => <MyFile key={name} fileId = {fileId} name= {name} type={type} date={date} username={username} URL={URL} size={size} approved={0} onView={() => {history.push(`/room/${roomId}/${fileId}`)}} onReview={reviewFile} isAdmin = {isAdmin}></MyFile>
                                        )}</IonList>)}
                                    {acceptedFiles.length === 0 &&  <IonList>
                                          <MyFile key = {"cheie"} fileId = {"id"} name = {"Momentan nu sunt fisiere in room"} type={"txt"} date={(new Date).toString()} username={"test"} URL={"url"} size={'0'} approved={0} onView={() => {}}> </MyFile>
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
                                        <IonItem> <input type="file" onChange={(ev) => onFileChange(ev)}></input>
                                        </IonItem>
                                        <IonInput placeholder="tags" value={tags} onIonInput={(e: any) => {tags = e.target.value}}/> 
                                        <IonButton color="primary" expand="full" onClick={() => submitForm("upload")}>
                                          Upload
                                        </IonButton>
                                        <IonButton color="primary" expand="full" onClick={() => submitForm("scan")}>
                                          Scan Notes
                                        </IonButton>
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