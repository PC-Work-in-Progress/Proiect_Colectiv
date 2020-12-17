export interface NotificationProps {
    userName: string;
    roomName: string;
    fileId: string;
    fileName: string;
    fileDate: string;
    tags: string[];
    onView: () => void;
}