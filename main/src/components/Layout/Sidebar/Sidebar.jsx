import React from "react";
import { motion } from "framer-motion";
import { FaBars } from 'react-icons/fa';
import NavItem from "../Sidebar/NavItem";
import { menuItems } from "../icons";
import { Tooltip } from "react-tooltip";

const Sidebar = () => {
    const [isOpen, setIsOpen] = React.useState(false);
    return <div>
        <motion.div initial={{width: 60}} 
        animate={{width: isOpen ? 210 : 60}}
        transition={{ duration: 0.4 }}
        style={{ backgroundColor: '#2b7b8c' }}
        className="h-screen text-white p-4 flex flex-col" >
            <button className="text-xl mb-8" onClick={ () => setIsOpen(prev => !prev)}>
                <FaBars />
            </button>

            <nav className={"flex flex-col gap-11 h-full}"}>
                {menuItems.map((item, index) => (
                    <NavItem
                        key={index}
                        icon={item.icon}
                        text={item.text}
                        path={item.path}
                        isOpen={isOpen}
                        setIsOpen={setIsOpen}
                    />
                ))}
            </nav>

        </motion.div>
        {!isOpen && <Tooltip id="sidebar-tooltip" offset={40} />}
    </div>;
};

export default Sidebar;