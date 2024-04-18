export interface Certificate {
    number: number;
    commonName: string;
    validFrom: Date;
    validUntil: Date;
    alias: string;
    issuerAlias: string;
    template: string;
    revoked: boolean;
}