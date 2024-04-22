export interface CertificateRequest {
    id?: number;
    commonName: string;
    firstName: string;
    lastName: string;
    organization: string;
    unit: string;
    country: string;
    uid: string;
    email: string;
}