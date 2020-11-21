import { IonButton, IonButtons, IonContent, IonIcon, IonTitle, IonToolbar } from "@ionic/react";
import { closeSharp } from "ionicons/icons";
import React, { useRef } from "react";
import { useState } from "react";
import { HideRoomFn } from "../home/CreateRoom";
import { getLogger } from "../shared";
import { UploadFileFn } from "./useRoomPage";

export type HideAddFileFn = () => void

const log = getLogger("UploadFile")

interface UploadFileProps {
    uploadFile: UploadFileFn;
    uploadFileError?: Error | null;
    uploading: boolean,
    hide: HideRoomFn
}

export const UploadFile: React.FC<UploadFileProps> = ({uploadFile, uploadFileError, uploading, hide}) => {
    const [file, setFile] = useState(null);
    const state = {
            selectedFile: null
    };

    const fileInput = useRef(null);;

    log("render")
    return (
        <IonContent class="popover-content">
            <IonToolbar>
                <IonTitle>Upload file</IonTitle>
                <IonButtons slot="end"><IonButton onClick={() => hide()}><IonIcon
                    icon={closeSharp}/></IonButton></IonButtons>
                <>
            <input
                ref={fileInput}
                hidden
                type="file"
                accept="image/*"
                onChange={() => {
                    console.log("onChange");
                }}
                onClick={() => {
                console.log('onClick');
                }}
            />
            <IonButton
                color="primary"
                onClick={() => {
                // @ts-ignore
                fileInput?.current?.click();
                // setBackgroundOption(BackgroundOptionType.Gradient);
                }}>
                Choose File
            </IonButton>
        </>
            </IonToolbar>
        </IonContent>
    )
}
