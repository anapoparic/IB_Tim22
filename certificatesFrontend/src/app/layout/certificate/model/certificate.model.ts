import { ReasonForRevoke } from "./enum/reasonForRevoke.enum";
import { Template } from "./enum/template.enum";

export interface Certificate {
    id?: number;
    validFrom?: Date;
    validTo?: Date;
    alias: string;
    issuerAlias: string;
    isRevoked?: boolean;
    reason?: ReasonForRevoke;
    template: Template;
    commonName: string;
    organization: string;
    organizationUnit: string,
    country: string,
    ownerEmail: string
}