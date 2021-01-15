import { IonButton, IonContent, IonItem, IonTextarea } from "@ionic/react";
import React, { useContext, useEffect } from "react";
import { useState } from "react";
import { ScanNotesFn, UploadFileFn } from "./useRoomPage";
import {scanNotes, scanNotes as scanNotesApi} from "./roomApi"
import { AuthContext } from "../auth/AuthProvider";
import "./ScanNotes.css"

export type HideRoomFn = () => void;

interface ScanNotesProps {
    uploadFile: UploadFileFn;
    file: FormData | undefined;
    text?: string;
    roomId: string;
    hide: HideRoomFn
}




export const ScanNotes: React.FC<ScanNotesProps> = ({uploadFile, file, roomId, hide}) => {
    const [text, setText] = useState("Waiting for file to be analysed");
    const [state, setFileState] = useState<ScanNotesProps>();
    const {token} = useContext(AuthContext);
    useEffect(scanNotesEffect,[file]);

    function sendFile(text: string) {
        var data = new Blob([text], {type: 'text/plain'});

        // If we are replacing a previously generated file we need to
        // manually revoke the object URL to avoid memory leaks.
        var formData = new FormData();
        var blob = new Blob([text], { type: "text/xml"});

       

        formData.append("file", blob, "MyNotes");

        uploadFile(formData, roomId, "tag");
    };
     

    function scanNotesEffect() {
        let canceled = false;
        scanNotesAsync();
        return () => {
            canceled = true;
        }

        async function scanNotesAsync() {
            if(file) {
                await scanNotes(token, file).then(result => {
                    setText(result)
                });
                
            }
        }
    }
    return (
        <IonContent class="popover-content">

            <div className="ion-padding-start ion-padding-bottom">
                <IonTextarea className="textAreaScanNotes" value={text} onIonChange={e => setText(e.detail.value!)} autoGrow = { true }></IonTextarea>
                <div className="button-div">
                <IonButton  onClick={() => sendFile(text)}> Upload </IonButton>
                </div>
                <div className="button-div">
                <IonButton  onClick={() => hide()}> Cancel </IonButton>
                </div>
            </div>
        </IonContent>
    );
}

