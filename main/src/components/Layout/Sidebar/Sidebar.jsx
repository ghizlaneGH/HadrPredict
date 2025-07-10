import React from "react";
import { motion } from "framer-motion";
import { FaBars, FaSignOutAlt } from "react-icons/fa"; // icône de déconnexion
import NavItem from "../Sidebar/NavItem";
import { menuItems } from "../icons";
import { Tooltip } from "react-tooltip";
import { useNavigate } from "react-router-dom";

const Sidebar = () => {
  const [isOpen, setIsOpen] = React.useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token"); // ou sessionStorage selon ton cas
    navigate("/LoginPage"); // redirection vers la page de connexion
  };

  return (
    <div>
      <motion.div
        initial={{ width: 60 }}
        animate={{ width: isOpen ? 210 : 60 }}
        transition={{ duration: 0.4 }}
        style={{ backgroundColor: "#2b7b8c" }}
        className="h-screen text-white p-4 flex flex-col justify-between"
      >
        {/* Header avec le bouton pour ouvrir/fermer */}
        <div>
          <button
            className="text-xl mb-8"
            onClick={() => setIsOpen((prev) => !prev)}
          >
            <FaBars />
          </button>

          {/* Menu items */}
          <nav className={"flex flex-col gap-11"}>
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
        </div>
        
      </motion.div>

      {!isOpen && <Tooltip id="sidebar-tooltip" offset={40} />}
    </div>
  );
};

export default Sidebar;
