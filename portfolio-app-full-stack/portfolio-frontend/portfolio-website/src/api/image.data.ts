import { useEffect, useState } from "react";

const useImageFetch = (imageUrl: string): ImageFetchResult => {
    const [imageState, setImageState ] = useState<ImageFetchResult>({
        imageUrl: null,
        loading: true,
        error: null,
    });

    useEffect(() => {
        const fetchImage = async () => {
            try {
                const response = await fetch(imageUrl);

                if (!response.ok) {
                    throw new Error(`Failed to fetch image: ${response.statusText}`);
                }

                const blob = await response.blob();
                const objectUrl = URL.createObjectURL(blob);
                setImageState({ imageUrl: objectUrl, loading: false, error: null });
            } catch (error) {
                if (error instanceof Error){
                    setImageState({ imageUrl: null, loading: false, error })
                } else {
                    setImageState({ imageUrl: null, loading: false, error: new Error("Unknown error occurred") });
                }
            }
        };

        fetchImage();

        return () => {
            URL.revokeObjectURL(imageState.imageUrl || '');
        };
    }, [imageUrl])

    return imageState;
};

export default useImageFetch;