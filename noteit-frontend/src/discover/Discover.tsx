import React from "react";
import {
    IonButtons,
    IonCard,
    IonCardContent,
    IonCardHeader,
    IonCardTitle,
    IonCol,
    IonContent,
    IonGrid,
    IonRow, IonSearchbar
} from '@ionic/react';
import {Header} from "../layout/Header";
import {getLogger} from "../shared";
import {RouteComponentProps} from "react-router";
import "./Discover.css"

const log = getLogger("Discover");

export const Discover: React.FC<RouteComponentProps> = ({history}) => {
    // const {state, createRoom, hideCreateRoom, showCreateRoom, nextPage, previousPage} = useHome();
    // const {
    //     rooms, user, recentFiles, showAddRoom, creatingError, creating, fetchingRoomsError, fetchingRooms,
    //     fetchingRecentFilesError, fetchingRecentFiles, fetchingUser, fetchingUserError, hasMoreNotifications, notificationsPage, previousNotifications
    // } = state;

    return (
        <IonContent class="fullscreen">
            <div className="flex-page">
                <Header/>
                <IonGrid class="grid-page ion-padding-bottom">
                    <IonRow class="fullscreen">
                        <IonCol class="fullscreen" offset="0.5" size="3">
                            <IonCard>
                                <IonCardHeader>
                                    <IonCardTitle>Filter</IonCardTitle>
                                </IonCardHeader>
                                <IonCardContent>
                                    {/*{rooms && (<IonList class="list-size">*/}
                                    {/*    {rooms.map(({id, name}) => <Room onView={() => {*/}
                                    {/*        history.push(`/room/${id}`)*/}
                                    {/*    }} name={name} id={id} key={id}/>)}*/}
                                    {/*</IonList>)}*/}
                                    {/*<IonLoading isOpen={fetchingRooms} message="Fetching rooms"/>*/}
                                    {/*{fetchingRoomsError &&*/}
                                    {/*<div className="create-room-error">{fetchingRoomsError.message}</div>}*/}
                                </IonCardContent>
                            </IonCard>
                        </IonCol>
                        <IonCol class="fullscreen" size="8">
                            <IonSearchbar showCancelButton="focus"/>
                            <IonCard class="notification-list-card">
                                <IonCardHeader>
                                    <div className="recent-files-title">
                                        {/*<IonButtons>*/}
                                        {/*<IonButton disabled={!previousNotifications}*/}
                                        {/*           onClick={async () => {*/}
                                        {/*               log("searchNext");*/}
                                        {/*               await previousPage?.();*/}
                                        {/*           }}>*/}
                                        {/*    <IonIcon icon={chevronBack}/>*/}
                                        {/*</IonButton>*/}
                                        {/*<span>{notificationsPage}</span>*/}
                                        {/*<IonButton disabled={!hasMoreNotifications}*/}
                                        {/*           onClick={async () => {*/}
                                        {/*               log("searchPrevious");*/}
                                        {/*               await nextPage?.();*/}
                                        {/*           }}>*/}
                                        {/*    <IonIcon icon={chevronForward}/>*/}
                                        {/*</IonButton>*/}
                                        {/*</IonButtons>*/}
                                    </div>
                                </IonCardHeader>
                                <IonCardContent>
                                    {/*<IonList class="notification-list">*/}
                                    {/*{recentFiles.length > 0 && (*/}
                                    {/*    recentFiles*/}
                                    {/*        .map(({fileId, fileName, roomName, userName, fileDate, tags, roomId}) =>*/}
                                    {/*            <Notification key={fileId}*/}
                                    {/*                          fileId={fileId}*/}
                                    {/*                          fileName={fileName}*/}
                                    {/*                          roomName={roomName}*/}
                                    {/*                          userName={userName}*/}
                                    {/*                          fileDate={fileDate}*/}
                                    {/*                          tags={tags}*/}
                                    {/*                          roomId={roomId}*/}
                                    {/*                          onView={() => {*/}
                                    {/*                              history.push(`/room/${roomId}/${fileId}`)*/}
                                    {/*                          }}/>)*/}
                                    {/*        .slice(notificationsPage * 15, getRecentFilesNr())*/}
                                    {/*)}*/}
                                    {/*</IonList>*/}
                                    {/*<IonLoading isOpen={fetchingRecentFiles} message="Fetching recent files"/>*/}
                                </IonCardContent>
                            </IonCard>
                            {/*{fetchingRecentFilesError &&*/}
                            {/*<div className="create-room-error">{fetchingRecentFilesError.message}</div>}*/}
                        </IonCol>
                    </IonRow>
                </IonGrid>
            </div>
        </IonContent>
    );

    // function getRecentFilesNr() {
    //     log("getRecentFilesNr");
    //     if (notificationsPage * 15 + 15 >= recentFiles.length) {
    //         return recentFiles.length;
    //     }
    //     return notificationsPage * 15 + 15;
    // }
}

