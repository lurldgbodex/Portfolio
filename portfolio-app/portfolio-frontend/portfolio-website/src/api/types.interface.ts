interface User {
    id: number;
    firstName: string;
    lastName: string;
    imageUrl: string;
    middleName?: string;
}

interface About {
    id: number;
    address: string;
    dob: string;
    title: string;
    phoneNumber: string;
    summary: string;
    email: string;
    github: string;
    linkedin: string;
    cv: string;
    medium?: string;
    twitter?: string;
    user: User;
}

interface CertificationDetail {
    id: number;
    details: string;
}

interface Certification {
    id: number,
    userId: number;
    name: string;
    body: string;
    data: string;
    details: CertificationDetail[]
}

interface Education {
    id: number;
    userId: number;
    school: string;
    degree: string;
    course: string;
    grade: string;
    description: string;
    startDate: string;
    endDate: string;
}

interface Experience {
    id: number;
    userId: number;
    company: string;
    role: string;
    description: string;
    startDate: string;
    endDate: string;
}

interface language {
    id: number;
    userId: number;
    lang: string;
    level: string;
}


interface Project {
    id: number;
    userId: number;
    name: string;
    type: string;
    url: string;
    description: string;
    imageLink: string;
}

interface SkillType {
    id: number;
    name: string;
}

interface Skills {
    id: number;
    userId: number;
    name: string;
    skills: SkillType []
}

interface ApiData {
    about: About
    certifications: Certification []
    educations: Education []
    experiences: Experience []
    languages: language []
    projects: Project []
    skills: Skills []
}

interface ImageFetchResult {
    imageUrl: string | null;
    loading: boolean;
    error: Error | null;
}