import { IonCard, IonContent, IonHeader, IonInfiniteScroll, IonInfiniteScrollContent, IonItem, IonList, IonPage, IonSearchbar, IonTitle, IonToolbar } from "@ionic/react";
import React, { useContext, useEffect, useState } from "react";
import { RouteComponentProps } from "react-router-dom";
import { baseUrl } from "../auth";

interface RoomProps extends RouteComponentProps<{
  id?: string;
}> {}

const RoomPage: React.FC<RoomProps> = ({ history }) => {

  const [files, setFiles] = useState<string[]>([]);
  const [searchFiles, setSearchFiles] = useState<string>('');
  const [disableInfiniteScroll, setDisableInfiniteScroll] = useState<boolean>(false);
  
  async function fetchData(reset?: boolean) {
    const fileList : string[] = reset ? [] : files;
    const url: string = `http://${baseUrl}/files`;    
    const res: Response = await fetch(url);
    res
      .json()
      .then(async (res) => {
        if (res && res.message && res.message.length > 0) {
          setFiles([...fileList, ...res.message]);
          setDisableInfiniteScroll(res.message.length < 10);
        } else {
          setDisableInfiniteScroll(true);
        }
      })
      .catch(err => console.error(err));
  }

  async function searchNext($event: CustomEvent<void>) {
    await fetchData();
    ($event.target as HTMLIonInfiniteScrollElement).complete();
  }

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Welcome to this Room</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent>
        <IonSearchbar
          value={searchFiles}
          debounce={1000}
          onIonChange={e => setSearchFiles(e.detail.value!)}>
        </IonSearchbar>
        <IonList>
          {files.map((item: string, i: number) => {
            return <IonCard key={`${i}`}><img src={item}/></IonCard>
          })}
          {files
            .filter(file => file.indexOf(searchFiles) >= 0)
            .map(file => <IonItem key={file}>{file}</IonItem>)}
        </IonList>
        <IonInfiniteScroll threshold="100px" disabled={disableInfiniteScroll}
                             onIonInfinite={(e: CustomEvent<void>) => searchNext(e)}>
            <IonInfiniteScrollContent
              loadingText="Loading more good files...">
            </IonInfiniteScrollContent>
          </IonInfiniteScroll>
      </IonContent>
    </IonPage>)
}

export default RoomPage
