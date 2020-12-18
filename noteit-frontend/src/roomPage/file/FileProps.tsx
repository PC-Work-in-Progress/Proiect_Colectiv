export interface FileProps {
    fileId: string;
    name: string;
    type: string;
    username: string;
    date: string;
    URL: string;
    size: string;
    onView?: () => void;
    onReview?: (fileId: string, type: string) => void;
    isAdmin?: boolean;
}