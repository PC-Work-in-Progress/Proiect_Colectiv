export interface NotificationProps {
    id: string;
    userName: string;
    roomId: string;
    roomName: string;
    fileId: string;
    fileName: string;
    time: Date;
    onView: () => void;
}