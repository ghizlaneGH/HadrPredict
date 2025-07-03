import React from "react";
import { useNavigate } from "react-router-dom";

const NavItem = ({ icon, text, isOpen, setIsOpen, path }) => {
    const navigate = useNavigate();
    const handleItemClick = () => {
        setIsOpen?.(false); 
        navigate(path);
    };
    const handleIconClick = (e) => {
        e.stopPropagation(); // Empeche le déclenchement de handleItemClick
        if (setIsOpen) setIsOpen(prev => !prev);
    };
return (
    <div onClick={handleItemClick}
        className="flex items-center gap-6 cursor-pointer w-full hover:text-orange-400">
        <span
            onClick={handleIconClick}
            data-tooltip-id={!isOpen ? "sidebar-tooltip" : undefined}
            data-tooltip-content={!isOpen ? text : undefined}
            className="text-xl"
        >
        {icon}
        </span>
        {isOpen && <div>{text}</div>}
    </div>
);
};

export default NavItem;