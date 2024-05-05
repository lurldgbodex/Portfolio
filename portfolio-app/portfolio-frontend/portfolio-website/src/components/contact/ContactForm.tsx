import axios from "axios";
import { ChangeEvent, FormEventHandler, useState } from "react"

import styles from './ContactForm.module.css'

interface ContactMeData {
    subject: string,
    email: string,
    message: string
}

const ContactForm = () => {
    const [formData, setFormData] = useState<ContactMeData>({
        subject: '',
        email: '',
        message: ''
    });

    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value});
    };

    const handleSubmit : FormEventHandler<HTMLFormElement> = async (e) => {
        e.preventDefault()        

        try {
            const response = await axios.post('/api/send-email', formData);
            console.log('Email sent successfully:', response.data);
            setFormData({ subject: '', email: '', message: '' });
        } catch (error) {
            console.error('Error sending email:',error);
        }
    };

    return(
        <div className={styles.contactContainer}>
            <h2 className={styles.title}>Contact Me</h2>
            <form onSubmit={handleSubmit} className={styles.form}>
                <div className={styles.inputContainer}>
                    <div className={styles.subjectContainer}>
                        <label htmlFor="subject"></label>
                        <input
                            type="text"
                            id="subject"
                            name="subject"
                            placeholder="subject"
                            value={formData.subject}
                            className={styles.formBox}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div className={styles.emailContainer}>
                        <label htmlFor="email"></label>
                        <input
                            type="email" 
                            id="email"
                            name="email"
                            value={formData.email}
                            placeholder="Enter email address"
                            className={styles.formBox}
                            onChange={handleChange}
                            required
                        />
                    </div>
                
                    <div className={styles.messageContainer}>
                        <label htmlFor="message"></label>
                        <textarea 
                            id="message"
                            name="message"
                            value={formData.message}
                            placeholder="Enter message..."
                            className={styles.message}
                            onChange={handleChange}
                            required
                        />
                    </div>
                </div>
                <div className={styles.buttonContainer}>
                    <button className={styles.button} type="submit">Send</button>
                </div>
            </form>
        </div>
    );
};

export default ContactForm;